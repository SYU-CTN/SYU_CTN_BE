package com.example.syu_ctn_be.chat.service;

import com.example.syu_ctn_be.chat.config.OpenAiProperties;
import com.example.syu_ctn_be.chat.dto.openai.OpenAiMessage;
import com.example.syu_ctn_be.chat.entity.ChatHistory;
import com.example.syu_ctn_be.chat.entity.ChatSession;
import com.example.syu_ctn_be.chat.entity.MessageRole;
import com.example.syu_ctn_be.chat.repository.ChatSessionRepository;
import com.example.syu_ctn_be.chat.repository.DocumentRepository;
import com.example.syu_ctn_be.domain.User;
import com.example.syu_ctn_be.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 상담 챗 도메인의 핵심 흐름을 담당한다.
 *  - 세션(대화방) 생성: 한 사용자는 동시에 최대 1개의 세션만 유지한다.
 *    새 채팅을 시작하면 기존 세션은 즉시 폐기되며, 페이지 이탈 시에도 명시적으로 폐기한다.
 *  - RAG: DocumentRepository 검색 → System 프롬프트 주입 → OpenAI 호출
 *  - 권한 검증: 모든 ask/delete 요청은 세션 소유자(USERS.LOGIN_ID) 본인만 가능
 *  - 대화 내역(USER/ASSISTANT) 영속화 및 첫 질문으로 제목 설정
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    /** 첫 질문에서 제목으로 사용할 최대 길이. */
    private static final int TITLE_MAX_LENGTH = 30;

    private final ChatSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final OpenAiClient openAiClient;
    private final OpenAiProperties openAiProperties;

    /**
     * 새 대화방을 생성한다.
     * 사용자에게 남아 있는 기존 세션이 있다면 모두 즉시 폐기한 뒤 새 세션을 만든다.
     * (페이지 이탈 시 DELETE 호출이 유실된 경우에도 다음 start 호출이 정리해 준다.)
     */
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

    /**
     * 사용자가 채팅 페이지를 이탈하는 등 명시적으로 세션을 폐기할 때 호출한다.
     * 본인 소유 세션이 아니거나 이미 사라진 세션은 거부한다.
     */
    @Transactional
    public void discardSession(String loginId, Long sessionId) {
        requireLoginId(loginId);
        if (sessionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId 가 비어 있습니다.");
        }

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

        if (!session.isOwnedBy(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 소유 세션이 아닙니다.");
        }

        sessionRepository.delete(session);
    }

    /**
     * 사용자 질문을 받아 RAG → OpenAI 호출 → 답변 저장 까지 처리한다.
     * 세션 소유자 검증을 통과하지 못하면 403 으로 즉시 차단한다.
     */
    @Transactional
    public String ask(String loginId, Long sessionId, String question) {
        requireLoginId(loginId);
        if (sessionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId 가 비어 있습니다.");
        }
        if (question == null || question.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문이 비어 있습니다.");
        }

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

        if (!session.isOwnedBy(loginId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 소유 세션이 아닙니다.");
        }

        String retrievedContext = documentRepository.findRelevantContext(question);
        String systemPrompt = buildSystemPrompt(retrievedContext);

        List<OpenAiMessage> openAiMessages = new ArrayList<>();
        openAiMessages.add(new OpenAiMessage("system", systemPrompt));
        openAiMessages.add(new OpenAiMessage("user", question));

        String answer = openAiClient.complete(openAiMessages);

        if (session.getTitle() == null || session.getTitle().isBlank()) {
            session.renameTo(summarizeForTitle(question));
        }
        session.addHistory(ChatHistory.of(MessageRole.USER, question));
        session.addHistory(ChatHistory.of(MessageRole.ASSISTANT, answer));

        return answer;
    }

    private String buildSystemPrompt(String retrievedContext) {
        String persona = openAiProperties.getSystemPersona();
        if (retrievedContext == null || retrievedContext.isBlank()) {
            return persona;
        }
        return persona
                + "\n\n다음은 답변에 활용할 내부 자료입니다. 자료에 근거하여 답하고, "
                + "근거가 부족하면 모른다고 답하세요.\n[자료]\n"
                + retrievedContext;
    }

    private String summarizeForTitle(String question) {
        String trimmed = question.strip();
        if (trimmed.length() <= TITLE_MAX_LENGTH) {
            return trimmed;
        }
        return trimmed.substring(0, TITLE_MAX_LENGTH) + "…";
    }

    private void requireLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }
    }
}
