package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.config.OpenAiProperties;
import com.example.syu_ctn_be.domain.User;
import com.example.syu_ctn_be.dto.openai.OpenAiMessage;
import com.example.syu_ctn_be.entity.ChatHistory;
import com.example.syu_ctn_be.entity.ChatSession;
import com.example.syu_ctn_be.entity.MessageRole;
import com.example.syu_ctn_be.repository.ChatSessionRepository;
import com.example.syu_ctn_be.repository.DocumentRepository;
import com.example.syu_ctn_be.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_HISTORY_MESSAGES = 12;

    private final ChatSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final OpenAiClient openAiClient;
    private final OpenAiProperties openAiProperties;

    @Transactional
    public Long startSession(String loginId) {
        requireLoginId(loginId);
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        List<ChatSession> existingSessions = sessionRepository.findAllByUser_LoginIdOrderByCreatedAtDesc(loginId);
        if (!existingSessions.isEmpty()) {
            sessionRepository.deleteAll(existingSessions);
        }

        ChatSession newSession = sessionRepository.save(ChatSession.openFor(user));
        return newSession.getId();
    }

    @Transactional
    public void discardSession(String loginId, Long sessionId) {
        requireLoginId(loginId);
        if (sessionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId가 비어 있습니다.");
        }

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

        if (!session.isOwnedBy(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 소유 세션이 아닙니다.");
        }

        sessionRepository.delete(session);
    }

    @Transactional
    public String ask(String loginId, Long sessionId, String question) {
        requireLoginId(loginId);
        if (sessionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId가 비어 있습니다.");
        }
        if (question == null || question.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문이 비어 있습니다.");
        }

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

        if (!session.isOwnedBy(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 소유 세션이 아닙니다.");
        }

        String normalizedQuestion = question.strip();
        String retrievedContext = documentRepository.findRelevantContext(normalizedQuestion);
        List<OpenAiMessage> messages = buildMessages(session, normalizedQuestion, retrievedContext);

        String answer = openAiClient.complete(messages);

        session.addHistory(ChatHistory.of(MessageRole.USER, normalizedQuestion));
        session.addHistory(ChatHistory.of(MessageRole.ASSISTANT, answer));

        return answer;
    }

    private List<OpenAiMessage> buildMessages(ChatSession session, String question, String retrievedContext) {
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(new OpenAiMessage("system", buildSystemPrompt(retrievedContext)));

        List<ChatHistory> histories = session.getHistories();
        int start = Math.max(0, histories.size() - MAX_HISTORY_MESSAGES);
        for (int i = start; i < histories.size(); i++) {
            ChatHistory history = histories.get(i);
            messages.add(new OpenAiMessage(toOpenAiRole(history.getRole()), history.getContent()));
        }

        messages.add(new OpenAiMessage("user", question));
        return messages;
    }

    private String buildSystemPrompt(String retrievedContext) {
        String persona = openAiProperties.getSystemPersona();
        if (persona == null || persona.isBlank()) {
            persona = "당신은 삼육대학교 학사 안내 AI 도우미입니다. 한국어로 친절하고 정확하게 답변하세요.";
        }

        return persona
                + "\n\n규칙:"
                + "\n1. 제공된 참고자료를 우선 근거로 사용하세요."
                + "\n2. 참고자료에 없는 내용은 추측하지 말고 모른다고 말하세요."
                + "\n3. 날짜, 학점, 장소, 절차처럼 중요한 정보는 짧고 명확하게 정리하세요."
                + "\n4. 학생이 다음에 해야 할 행동이 있으면 마지막에 안내하세요."
                + "\n\n[참고자료]\n"
                + (retrievedContext == null || retrievedContext.isBlank()
                ? "현재 질문과 직접 관련된 참고자료를 찾지 못했습니다."
                : retrievedContext);
    }

    private String toOpenAiRole(MessageRole role) {
        return role == MessageRole.ASSISTANT ? "assistant" : "user";
    }

    private void requireLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }
    }
}
