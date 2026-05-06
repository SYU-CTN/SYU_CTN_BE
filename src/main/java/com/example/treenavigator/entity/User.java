package com.example.treenavigator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "users")
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
    private Role role = Role.STUDENT;

    // 👇 이 부분이 빠져서 생긴 에러입니다! 다시 추가했습니다.
    public static User registerUser(String loginId, String password, String name, String email,
                                    String department, Integer grade, String phone, String userType) {
        User user = new User();
        user.setLoginId(loginId);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        user.setDepartment(department);
        user.setGrade(grade);
        user.setPhone(phone);
        user.setUserType(userType);
        user.setRole(Role.STUDENT);
        return user;
    }
}

enum Role {
    STUDENT, ADMIN
}