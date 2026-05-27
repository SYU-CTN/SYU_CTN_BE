package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.AskRequest;
import com.example.syu_ctn_be.dto.AskResponse;
import com.example.syu_ctn_be.dto.StartSessionResponse;
import com.example.syu_ctn_be.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/chat/sessions")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start")
    public StartSessionResponse start(@AuthenticationPrincipal UserDetails principal) {
        String loginId = resolveLoginId(principal);
        Long sessionId = chatService.startSession(loginId);
        return new StartSessionResponse(sessionId);
    }

    @PostMapping("/ask")
    public AskResponse ask(@AuthenticationPrincipal UserDetails principal,
                           @RequestBody AskRequest request) {
        String loginId = resolveLoginId(principal);
        String answer = chatService.ask(loginId, request.getSessionId(), request.getQuestion());
        return new AskResponse(request.getSessionId(), answer);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void discard(@AuthenticationPrincipal UserDetails principal,
                        @PathVariable Long sessionId) {
        String loginId = resolveLoginId(principal);
        chatService.discardSession(loginId, sessionId);
    }

    private String resolveLoginId(UserDetails principal) {
        // SecurityFilter 가 통과시켰더라도 방어적으로 한 번 더 확인한다.
        // UserDetailsService 가 USERS.LOGIN_ID 를 username 으로 채워 넣는다는 전제이다.
        if (principal == null || principal.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        return principal.getUsername();
    }
}
