package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.LoginRequestDto;
import com.example.syu_ctn_be.dto.SignUpRequestDto;
import com.example.syu_ctn_be.entity.User;
import com.example.syu_ctn_be.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @RequestParam String loginId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String tokenLoginId = extractLoginIdFromAuthorizationHeader(authorizationHeader);
        if (!loginId.equals(tokenLoginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 계정만 삭제할 수 있습니다.");
        }

        authService.deleteMe(loginId);
        return ResponseEntity.noContent().build();
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

    private String extractLoginIdFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }

        return extractLoginIdFromJwt(token).orElse(token);
    }

    private Optional<String> extractLoginIdFromJwt(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return Optional.empty();
        }

        try {
            byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = OBJECT_MAPPER.readTree(new String(decodedPayload, StandardCharsets.UTF_8));
            JsonNode loginId = payload.hasNonNull("loginId") ? payload.get("loginId") : payload.get("sub");
            if (loginId == null || loginId.asText().isBlank()) {
                return Optional.empty();
            }
            return Optional.of(loginId.asText());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
