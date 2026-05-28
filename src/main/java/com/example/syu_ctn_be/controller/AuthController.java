package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.LoginRequestDto;
import com.example.syu_ctn_be.dto.SignUpRequestDto;
import com.example.syu_ctn_be.entity.User;
import com.example.syu_ctn_be.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam("loginId") String loginId) {
        boolean isDuplicate = authService.checkIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDto request) {
        authService.registerNewUser(request);
        return ResponseEntity.ok("signup success");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        boolean isAuthenticated = authService.authenticateUser(request);
        if (isAuthenticated) {
            return ResponseEntity.ok("login success");
        }
        return ResponseEntity.status(401).body("login failed");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@RequestParam String loginId) {
        User user = authService.getUserInfo(loginId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request) {
        String loginId = request.get("loginId");
        String newPassword = request.get("password");

        authService.updatePassword(loginId, newPassword);
        return ResponseEntity.ok("password updated");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("logout success");
    }
}
