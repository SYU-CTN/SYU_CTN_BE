package com.example.syu_ctn_be.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 서비스 사용자(USERS 테이블) 엔티티.
 * 외부에서 사용자를 식별할 때는 LOGIN_ID 를 기준으로 한다.
 */
@Entity
@Getter
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    /** 로그인/세션 식별에 사용되는 외부 식별자. */
    @Column(name = "LOGIN_ID", nullable = false, unique = true, length = 100)
    private String loginId;

    @Builder
    private User(String loginId) {
        this.loginId = loginId;
    }
}
