package com.example.treenavigator.repository;

import com.example.treenavigator.entity.User; // 1. 경로 확인!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이미 있는 코드: 로그인 시 아이디로 사용자 찾기
    Optional<User> findByLoginId(String loginId);

    // 추가할 코드: 회원가입 시 아이디 중복 여부 확인
    boolean existsByLoginId(String loginId);
}