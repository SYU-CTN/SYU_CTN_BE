package com.example.treenavigator.controller;

import com.example.treenavigator.dto.LoginRequestDto;
import com.example.treenavigator.dto.SignUpRequestDto;
import com.example.treenavigator.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버의 접근 허용
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDto request) {
        authService.registerNewUser(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        boolean isAuthenticated = authService.authenticateUser(request);
        if (isAuthenticated) {
            return ResponseEntity.ok("로그인 성공"); // 실제로는 JWT 토큰 등을 반환해야 합니다.
        }
        return ResponseEntity.status(401).body("로그인 실패");
    }
}