package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.LoginRequestDto;
import com.example.syu_ctn_be.dto.SignUpRequestDto;
import com.example.syu_ctn_be.entity.User;
import com.example.syu_ctn_be.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth") // 🌟 여기를 /api/v1/auth 로 수정!
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    // 아이디(학번/사번) 중복 체크 API
    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam("loginId") String loginId) {
        boolean isDuplicate = authService.checkIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate);
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDto request) {
        authService.registerNewUser(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        boolean isAuthenticated = authService.authenticateUser(request);
        if (isAuthenticated) {
            return ResponseEntity.ok("로그인 성공");
        }
        return ResponseEntity.status(401).body("로그인 실패");
    }

    // 내 정보 조회 API (학번으로 조회)
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@RequestParam String loginId) {
        User user = authService.getUserInfo(loginId);
        return ResponseEntity.ok(user);
    }

    // 비밀번호 변경 API
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request) {
        String loginId = request.get("loginId");
        String newPassword = request.get("password");

        authService.updatePassword(loginId, newPassword);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃 성공");
    }
}