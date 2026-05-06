package com.example.syu_ctn_be.chat.controller;

import com.example.syu_ctn_be.chat.dto.AskRequest;
import com.example.syu_ctn_be.chat.dto.AskResponse;
import com.example.syu_ctn_be.chat.dto.StartSessionResponse;
import com.example.syu_ctn_be.chat.service.ChatService;
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

/**
 * 상담 챗 엔드포인트.
 * 모든 호출은 인증된 사용자만 접근 가능 (Spring Security 설정에서 별도 시큐어 처리 필요).
 * 세션 소유자는 USERS.LOGIN_ID 로 식별한다 (Spring Security UserDetails.username 에 LOGIN_ID 가 매핑되어 있음을 전제로 한다).
 * 세션은 사용자당 최대 1개만 유지되며, 새 채팅을 시작하거나 채팅 페이지를 이탈하면 즉시 폐기된다.
 */
@RestController
@RequestMapping("/api/v1/chat/sessions")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 새 대화방(세션)을 생성한다.
     */
    @PostMapping("/start")
    public StartSessionResponse start(@AuthenticationPrincipal UserDetails principal) {
        String loginId = resolveLoginId(principal);
        Long sessionId = chatService.startSession(loginId);
        return new StartSessionResponse(sessionId);
    }

    /**
     * RAG + OpenAI 답변을 반환한다. 세션 소유자 본인만 호출할 수 있다.
     */
    @PostMapping("/ask")
    public AskResponse ask(@AuthenticationPrincipal UserDetails principal,
                           @RequestBody AskRequest request) {
        String loginId = resolveLoginId(principal);
        String answer = chatService.ask(loginId, request.getSessionId(), request.getQuestion());
        return new AskResponse(request.getSessionId(), answer);
    }

    /**
     * 사용자가 채팅 페이지를 이탈할 때 호출한다.
     * 세션과 그 하위 대화 내역까지 즉시 영구 삭제한다 (cascade).
     * 프론트엔드는 페이지 unload 시점에 navigator.sendBeacon 또는 fetch keepalive 로 호출한다.
     */
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
