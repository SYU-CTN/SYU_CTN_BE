package com.example.syu_ctn_be.dto; // 🌟 1. 이 파일의 소속(패키지)을 명시합니다.

// 🌟 2. 필요한 기능들을 밖에서 가져옵니다 (import)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {

    // 1. users 테이블에서 가져올 내 정보
    private String name;
    private String loginId;
    private String department;
    private String email;

    // 2. enrollments와 courses를 조인해서 가져올 '내가 수강한 과목' 리스트
    private List<MyCourseDto> myCourses;

    // 🌟 3. 한 파일 안에서 깔끔하게 관리하기 위해 클래스 안에 내부 클래스로 넣었습니다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCourseDto {
        // courses 테이블에서 가져올 정보
        private Long courseId;
        private String title;
        private Integer credits;
        private String category;

        // enrollments 테이블에서 가져올 정보
        private String semester;

        // 이수 여부 (임시 필드)
        private boolean isCompleted;
    }
}