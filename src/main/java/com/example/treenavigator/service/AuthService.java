package com.example.treenavigator.service;

import com.example.treenavigator.entity.User;
import com.example.treenavigator.dto.LoginRequestDto;
import com.example.treenavigator.dto.SignUpRequestDto;
import com.example.treenavigator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public void registerNewUser(SignUpRequestDto request) {
        // 중복 체크 로직 (생략)

        User newUser = User.registerUser(
                request.getLoginId(),
                request.getPassword(),
                request.getName(),
                request.getUserType().name(),
                request.getDepartment(),
                request.getGrade(),
                request.getEmail(),
                request.getPhoneNumber()
        );

        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public boolean authenticateUser(LoginRequestDto request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return user.getPassword().equals(request.getPassword());
    }
}