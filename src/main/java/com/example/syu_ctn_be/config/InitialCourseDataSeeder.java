package com.example.TreeNavigator.syu_ctn_be.config;

import com.example.TreeNavigator.syu_ctn_be.domain.Course;
import com.example.TreeNavigator.syu_ctn_be.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InitialCourseDataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            return;
        }

        Map<String, Course> courses = new LinkedHashMap<>();
        addCourses(courses);
        courseRepository.saveAll(courses.values());

        addPrerequisites(courses);
        courseRepository.saveAll(courses.values());
    }

    private void addCourses(Map<String, Course> courses) {
        add(courses, "C01", "소프트웨어원리", 1, "공통", 0, 0);
        add(courses, "C02", "컴퓨터프로그래밍", 2, "공통", 0, 90);
        add(courses, "C03", "컴퓨터프로그래밍응용", 2, "공통", 0, 180);
        add(courses, "C04", "자료구조", 2, "공통", 0, 270);
        add(courses, "C05", "컴퓨터알고리즘", 3, "공통", 0, 360);
        add(courses, "C06", "오픈소스SW이해와활용", 2, "공통", 0, 450);
        add(courses, "C07", "AI응용 윈도우프로그래밍", 2, "공통", 0, 540);
        add(courses, "C08", "창의적공학설계", 2, "공통", 0, 630);
        add(courses, "C09", "확률통계", 2, "공통", 0, 720);
        add(courses, "C10", "선형대수", 2, "공통", 0, 810);
        add(courses, "C11", "산학협력캡스톤디자인I", 3, "공통", 0, 900);
        add(courses, "C12", "산학협력캡스톤디자인II", 4, "공통", 0, 990);
        add(courses, "C13", "산학협력캡스톤디자인III", 4, "공통", 0, 1080);
        add(courses, "C14", "종합시험(P)", 4, "공통", 0, 1170, 0);
        add(courses, "C15", "기업가정신과창업", 4, "공통", 0, 1260);
        add(courses, "C16", "ICT인턴십(I,II,III,IV)", 4, "공통", 0, 1350);
        add(courses, "C17", "ICT멘토링프로젝트", 3, "공통", 0, 1440);

        add(courses, "S01", "UX프로그래밍", 1, "SW전공", 360, 0);
        add(courses, "S02", "객체지향프로그래밍I", 2, "SW전공", 360, 90);
        add(courses, "S03", "객체지향프로그래밍II", 2, "SW전공", 360, 180);
        add(courses, "S04", "웹프로그래밍", 2, "SW전공", 360, 270);
        add(courses, "S05", "프로그래밍언어론", 3, "SW전공", 360, 360);
        add(courses, "S06", "컴파일러", 3, "SW전공", 360, 450);
        add(courses, "S07", "모바일프로그래밍", 3, "SW전공", 360, 540);
        add(courses, "S08", "인공지능", 3, "SW전공", 360, 630);
        add(courses, "S09", "기계학습", 4, "SW전공", 360, 720);
        add(courses, "S10", "AI를 위한 인간컴퓨터 상호작용", 4, "SW전공", 360, 810);
        add(courses, "S11", "소프트웨어공학", 3, "SW전공", 360, 900);
        add(courses, "S12", "소프트웨어 디자인 패턴", 4, "SW전공", 360, 990);
        add(courses, "S13", "데이터베이스", 3, "SW전공", 360, 1080);
        add(courses, "S14", "데이터베이스 프로그래밍", 3, "SW전공", 360, 1170);
        add(courses, "S15", "AI를 위한 빅데이터 처리", 4, "SW전공", 360, 1260);
        add(courses, "S16", "AI를 위한 빅데이터 분석 및 표현", 4, "SW전공", 360, 1350);

        add(courses, "E01", "AI를 위한 미적분학", 1, "컴공전공", 720, 0);
        add(courses, "E02", "AI를 위한 이산수학", 1, "컴공전공", 720, 90);
        add(courses, "E03", "AIoT프로그래밍", 1, "컴공전공", 720, 180);
        add(courses, "E04", "디지털논리회로", 2, "컴공전공", 720, 270);
        add(courses, "E05", "컴퓨터구조", 2, "컴공전공", 720, 360);
        add(courses, "E06", "데이터통신", 2, "컴공전공", 720, 450);
        add(courses, "E07", "운영체제", 3, "컴공전공", 720, 540);
        add(courses, "E08", "리눅스시스템", 3, "컴공전공", 720, 630);
        add(courses, "E09", "AI 임베디드시스템", 4, "컴공전공", 720, 720);
        add(courses, "E10", "실시간운영체제", 4, "컴공전공", 720, 810);
        add(courses, "E11", "AIoT프로그래밍II", 4, "컴공전공", 720, 900);
        add(courses, "E12", "컴퓨터네트워크", 3, "컴공전공", 720, 990);
        add(courses, "E13", "네트워크 프로그래밍", 3, "컴공전공", 720, 1080);
        add(courses, "E14", "모바일네트워크", 4, "컴공전공", 720, 1170);
        add(courses, "E15", "네트워크엔지니어링", 4, "컴공전공", 720, 1260);
        add(courses, "E16", "시스템프로그래밍", 3, "컴공전공", 720, 1350);
        add(courses, "E17", "AI를 위한 클라우드 컴퓨팅", 3, "컴공전공", 720, 1440);
        add(courses, "E18", "멀티미디어", 3, "컴공전공", 720, 1530);
        add(courses, "E19", "디지털영상처리", 3, "컴공전공", 720, 1620);
        add(courses, "E20", "컴퓨터그래픽스", 4, "컴공전공", 720, 1710);
        add(courses, "E21", "정보보호및보안", 4, "컴공전공", 720, 1800);
    }

    private void addPrerequisites(Map<String, Course> courses) {
        link(courses, "C02", "C01");
        link(courses, "C03", "C02");
        link(courses, "C04", "C02");
        link(courses, "C05", "C04");
        link(courses, "C06", "C01");
        link(courses, "C07", "C06");
        link(courses, "S01", "C01");
        link(courses, "S02", "C01");
        link(courses, "S03", "S02");
        link(courses, "S04", "S03");
        link(courses, "S05", "S03");
        link(courses, "S06", "S05");
        link(courses, "S07", "S04");
        link(courses, "S08", "C05");
        link(courses, "S09", "S08");
        link(courses, "S10", "S09");
        link(courses, "S10", "S07");
        link(courses, "S11", "C05");
        link(courses, "S12", "S11");
        link(courses, "S14", "S13");
        link(courses, "S15", "S14");
        link(courses, "S16", "S15");
        link(courses, "E02", "E01");
        link(courses, "E03", "C01");
        link(courses, "E04", "E03");
        link(courses, "E05", "E04");
        link(courses, "E06", "E04");
        link(courses, "E07", "E05");
        link(courses, "E08", "E07");
        link(courses, "E09", "E08");
        link(courses, "E10", "E09");
        link(courses, "E11", "E08");
        link(courses, "E12", "E06");
        link(courses, "E13", "E12");
        link(courses, "E14", "E13");
        link(courses, "E15", "E14");
        link(courses, "E19", "E18");
        link(courses, "E20", "E19");
        link(courses, "E21", "E20");
    }

    private void add(Map<String, Course> courses, String code, String title, int gradeLevel, String category, float posX, float posY) {
        add(courses, code, title, gradeLevel, category, posX, posY, 3);
    }

    private void add(Map<String, Course> courses, String code, String title, int gradeLevel, String category, float posX, float posY, int credits) {
        int semester = posY % 180 == 0 ? 1 : 2;
        Course course = Course.builder()
                .courseCode(code)
                .title(title)
                .credits(credits)
                .gradeLevel(gradeLevel)
                .semester(semester)
                .category(category)
                .trackName(category)
                .description(category + " " + gradeLevel + "학년 권장 과목")
                .recommendation(0)
                .posX(posX)
                .posY(posY)
                .prerequisiteCourses(new ArrayList<>())
                .build();
        courses.put(code, course);
    }

    private void link(Map<String, Course> courses, String postCode, String preCode) {
        Course postCourse = courses.get(postCode);
        Course preCourse = courses.get(preCode);
        if (postCourse != null && preCourse != null) {
            postCourse.getPrerequisiteCourses().add(preCourse);
        }
    }
}
