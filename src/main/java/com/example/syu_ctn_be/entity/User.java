package com.example.syu_ctn_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 규칙: 기본 생성자 접근 제어
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginId;

    private String password;
    private String name;
    private String email;
    private String department;
    private Integer grade;
    private String phone;
    private String userType;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 🌟 1. Role Enum을 다른 파일에서도 쓸 수 있도록 클래스 안으로 넣고 public을 붙였습니다!
    public enum Role {
        STUDENT, ADMIN
    }

    // --- [새로 추가된 메서드] ---
    /**
     * 비밀번호를 변경하는 비즈니스 로직입니다.
     * 무분별한 @Setter 대신 의미 있는 이름의 메서드를 사용합니다.
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    // -------------------------

    // 회원가입 시 객체를 생성하는 정적 팩토리 메서드
    public static User registerUser(String loginId, String password, String name, String email,
                                    String department, Integer grade, String phone, String userType) {
        User user = new User();
        user.loginId = loginId;
        user.password = password;
        user.name = name;
        user.email = email;
        user.department = department;
        user.grade = grade;
        user.phone = phone;
        user.userType = userType;

        // 🌟 2. userType이 "STAFF"면 ADMIN 권한을, 아니면 STUDENT 권한을 주도록 수정했습니다!
        if ("STAFF".equals(userType)) {
            user.role = Role.ADMIN;
        } else {
            user.role = Role.STUDENT;
        }

        return user;
    }
}