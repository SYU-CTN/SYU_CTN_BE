package com.example.syu_ctn_be.dto;

import com.example.syu_ctn_be.domain.Course;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 과목 관련 데이터 전송 객체
 */
public class CourseDTO {

    /** 과목 등록/수정 요청 */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "과목 코드는 필수입니다")
        @Size(max = 20, message = "과목 코드는 20자 이하여야 합니다")
        private String code;

        @NotBlank(message = "과목명은 필수입니다")
        @Size(max = 100, message = "과목명은 100자 이하여야 합니다")
        private String title;

        @NotNull(message = "학점은 필수입니다")
        @Min(value = 0, message = "학점은 0 이상이어야 합니다")
        @Max(value = 20, message = "학점은 20 이하여야 합니다")
        private Integer credits;

        @NotNull(message = "학년은 필수입니다")
        @Min(value = 1) @Max(value = 4)
        private Integer grade;

        @NotNull(message = "학기는 필수입니다")
        @Min(value = 1) @Max(value = 2)
        private Integer semester;

        @NotBlank(message = "구분은 필수입니다")
        private String category;

        public Course toEntity() {
            return Course.builder()
                    .code(code)
                    .title(title)
                    .credits(credits)
                    .grade(grade)
                    .semester(semester)
                    .category(category)
                    .build();
        }
    }

    /** 과목 응답 */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String code;
        private String title;
        private Integer credits;
        private Integer grade;
        private Integer semester;
        private String category;

        public static Response from(Course course) {
            return Response.builder()
                    .id(course.getId())
                    .code(course.getCode())
                    .title(course.getTitle())
                    .credits(course.getCredits())
                    .grade(course.getGrade())
                    .semester(course.getSemester())
                    .category(course.getCategory())
                    .build();
        }
    }
}