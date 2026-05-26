package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.dto.PrerequisiteDTO;
import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.domain.Prerequisite;
import com.example.syu_ctn_be.exception.DuplicateResourceException;
import com.example.syu_ctn_be.exception.ResourceNotFoundException;
import com.example.syu_ctn_be.repository.CourseRepository;
import com.example.syu_ctn_be.repository.PrerequisiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrerequisiteService {

    private final PrerequisiteRepository prerequisiteRepository;
    private final CourseRepository courseRepository;

    /** 전체 선수관계 조회 */
    public List<PrerequisiteDTO.Response> getAllPrerequisites() {
        return prerequisiteRepository.findAll().stream()
                .map(PrerequisiteDTO.Response::from)
                .toList();
    }

    /** 특정 과목의 선수 과목들 (이 과목을 듣기 위해 먼저 들어야 하는 것들) */
    public List<PrerequisiteDTO.Response> getPrerequisitesOf(Long courseId) {
        return prerequisiteRepository.findByPostCourseId(courseId).stream()
                .map(PrerequisiteDTO.Response::from)
                .toList();
    }

    /** 특정 과목의 후속 과목들 */
    public List<PrerequisiteDTO.Response> getNextCoursesOf(Long courseId) {
        return prerequisiteRepository.findByPreCourseId(courseId).stream()
                .map(PrerequisiteDTO.Response::from)
                .toList();
    }

    /** 선수관계 등록 */
    @Transactional
    public PrerequisiteDTO.Response createPrerequisite(PrerequisiteDTO.Request request) {
        Long preId = request.getPreCourseId();
        Long postId = request.getPostCourseId();

        // 자기 자신을 선수로 등록하는 것 방지
        if (preId.equals(postId)) {
            throw new IllegalArgumentException("선수 과목과 후속 과목이 같을 수 없습니다");
        }

        // 중복 체크
        if (prerequisiteRepository.existsByPreCourseIdAndPostCourseId(preId, postId)) {
            throw new DuplicateResourceException("이미 등록된 선수관계입니다");
        }

        // 순환 참조 검사 (post → ... → pre 경로가 존재하면 사이클)
        if (causesCycle(preId, postId)) {
            throw new IllegalArgumentException("선수관계에 순환이 발생합니다");
        }

        Course preCourse = courseRepository.findById(preId)
                .orElseThrow(() -> new ResourceNotFoundException("선수 과목을 찾을 수 없습니다. id=" + preId));
        Course postCourse = courseRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("후속 과목을 찾을 수 없습니다. id=" + postId));

        Prerequisite saved = prerequisiteRepository.save(
                Prerequisite.builder()
                        .preCourse(preCourse)
                        .postCourse(postCourse)
                        .build()
        );
        return PrerequisiteDTO.Response.from(saved);
    }

    /** 선수관계 삭제 (id로) */
    @Transactional
    public void deletePrerequisite(Long id) {
        if (!prerequisiteRepository.existsById(id)) {
            throw new ResourceNotFoundException("선수관계를 찾을 수 없습니다. id=" + id);
        }
        prerequisiteRepository.deleteById(id);
    }

    /** 선수관계 삭제 (preId, postId 쌍으로) */
    @Transactional
    public void deletePrerequisiteByPair(Long preId, Long postId) {
        Prerequisite p = prerequisiteRepository.findByPreCourseIdAndPostCourseId(preId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("선수관계를 찾을 수 없습니다"));
        prerequisiteRepository.delete(p);
    }

    /**
     * 순환 참조 검사: 새 선수관계 (preId → postId)를 추가했을 때 사이클이 생기는지 확인
     * postId에서 시작해서 DFS로 preId에 도달할 수 있으면 사이클
     */
    private boolean causesCycle(Long preId, Long postId) {
        Set<Long> visited = new HashSet<>();
        return dfs(postId, preId, visited);
    }

    private boolean dfs(Long current, Long target, Set<Long> visited) {
        if (current.equals(target)) return true;
        if (!visited.add(current)) return false;

        List<Prerequisite> nexts = prerequisiteRepository.findByPreCourseId(current);
        for (Prerequisite next : nexts) {
            if (dfs(next.getPostCourse().getId(), target, visited)) {
                return true;
            }
        }
        return false;
    }
}