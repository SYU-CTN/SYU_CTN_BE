package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.AskRequest;
import com.example.syu_ctn_be.dto.AskResponse;
import com.example.syu_ctn_be.dto.StartSessionResponse;
import com.example.syu_ctn_be.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/chat/sessions")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start")
    public StartSessionResponse start(@RequestParam(required = false) String loginId) {
        validateLoginId(loginId);
        Long sessionId = chatService.startSession(loginId);
        return new StartSessionResponse(sessionId);
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestParam(required = false) String loginId, @RequestBody AskRequest request) {
        validateLoginId(loginId);
        String answer = chatService.ask(loginId, request.getSessionId(), request.getQuestion());
        return new AskResponse(request.getSessionId(), answer);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void discard(@RequestParam(required = false) String loginId, @PathVariable Long sessionId) {
        validateLoginId(loginId);
        chatService.discardSession(loginId, sessionId);
    }

    private void validateLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginId is required.");
        }
    }
}
