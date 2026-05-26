package com.example.syu_ctn_be.dto;
import com.example.syu_ctn_be.domain.Prerequisite;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class PrerequisiteDTO {

    /** 선수관계 등록 요청 */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "선수 과목 ID는 필수입니다")
        private Long preCourseId;

        @NotNull(message = "후속 과목 ID는 필수입니다")
        private Long postCourseId;
    }

    /** 선수관계 응답 (간단 버전: id 쌍) */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long preCourseId;
        private String preCourseCode;
        private String preCourseTitle;
        private Long postCourseId;
        private String postCourseCode;
        private String postCourseTitle;

        public static Response from(Prerequisite p) {
            return Response.builder()
                    .id(p.getId())
                    .preCourseId(p.getPreCourse().getId())
                    .preCourseCode(p.getPreCourse().getCode())
                    .preCourseTitle(p.getPreCourse().getTitle())
                    .postCourseId(p.getPostCourse().getId())
                    .postCourseCode(p.getPostCourse().getCode())
                    .postCourseTitle(p.getPostCourse().getTitle())
                    .build();
        }
    }
}
