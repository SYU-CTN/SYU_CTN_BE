package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.PrerequisiteDTO;
import com.example.syu_ctn_be.service.PrerequisiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("V1/api/prerequisites")
@RequiredArgsConstructor
public class PrerequisiteController {

    private final PrerequisiteService prerequisiteService;

    /**
     * 전체 선수관계 조회 (메인페이지 트리 시각화용)
     * GET /api/prerequisites
     */
    @GetMapping
    public ResponseEntity<List<PrerequisiteDTO.Response>> getAll() {
        return ResponseEntity.ok(prerequisiteService.getAllPrerequisites());
    }

    /** 특정 과목의 선수 과목들 조회 */
    @GetMapping("/of/{courseId}/prereqs")
    public ResponseEntity<List<PrerequisiteDTO.Response>> getPrerequisitesOf(@PathVariable Long courseId) {
        return ResponseEntity.ok(prerequisiteService.getPrerequisitesOf(courseId));
    }

    /** 특정 과목의 후속 과목들 조회 */
    @GetMapping("/of/{courseId}/next")
    public ResponseEntity<List<PrerequisiteDTO.Response>> getNextCoursesOf(@PathVariable Long courseId) {
        return ResponseEntity.ok(prerequisiteService.getNextCoursesOf(courseId));
    }

    /** 선수관계 등록 */
    @PostMapping
    public ResponseEntity<PrerequisiteDTO.Response> create(@Valid @RequestBody PrerequisiteDTO.Request request) {
        PrerequisiteDTO.Response created = prerequisiteService.createPrerequisite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** 선수관계 삭제 (id로) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prerequisiteService.deletePrerequisite(id);
        return ResponseEntity.noContent().build();
    }

    /** 선수관계 삭제 (preId, postId 쌍으로) */
    @DeleteMapping
    public ResponseEntity<Void> deleteByPair(
            @RequestParam Long preId,
            @RequestParam Long postId
    ) {
        prerequisiteService.deletePrerequisiteByPair(preId, postId);
        return ResponseEntity.noContent().build();
    }
}