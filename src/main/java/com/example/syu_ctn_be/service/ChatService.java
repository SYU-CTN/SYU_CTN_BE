package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.entity.ChatHistory;
import com.example.syu_ctn_be.entity.ChatSession;
import com.example.syu_ctn_be.entity.MessageRole;
import com.example.syu_ctn_be.repository.ChatSessionRepository;
import com.example.syu_ctn_be.domain.User;
import com.example.syu_ctn_be.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChatService {

  private static final int TITLE_MAX_LENGTH = 30;

  private final ChatSessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final VectorStore vectorStore; // ✨ 기존 DocumentRepository 대신 Chroma DB 활용
  private final ChatClient chatClient;   // ✨ 정품 Spring AI Fluent API 활용

  // 빌더를 통해 안정적으로 ChatClient를 주입받습니다.
  public ChatService(ChatSessionRepository sessionRepository,
      UserRepository userRepository,
      VectorStore vectorStore,
      ChatClient.Builder chatClientBuilder) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
    this.vectorStore = vectorStore;
    this.chatClient = chatClientBuilder.build();
  }

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
    if (sessionId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId 가 비어 있습니다.");

    ChatSession session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

    if (!session.isOwnedBy(loginId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 소유 세션이 아닙니다.");

    sessionRepository.delete(session);
  }

  @Transactional
  public String ask(String loginId, Long sessionId, String question) {
    requireLoginId(loginId);
    if (sessionId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId 가 비어 있습니다.");
    if (question == null || question.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문이 비어 있습니다.");

    ChatSession session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

    if (!session.isOwnedBy(loginId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 소유 세션이 아닙니다.");

    // 1. [RAG] 전 단계에서 고쳤던 표준 SearchRequest 빌더 문법으로 Chroma DB 검색
    var searchResults = vectorStore.similaritySearch(
        SearchRequest.builder()
            .query(question)
            .topK(3) // 관련된 문서 상위 3개 추출
            .build()
    );

    String retrievedContext = searchResults.stream()
        .map(org.springframework.ai.document.Document::getText)
        .collect(Collectors.joining("\n"));

    // 2. OpenAI로 보낼 대화 상자(List<Message>) 리스트 조립
    List<Message> openAiMessages = new java.util.ArrayList<>();

    // 2-1. 시스템 프롬프트 (페르소나 + 참고용 지식 데이터 백킹)
    String systemPrompt = buildSystemPrompt(retrievedContext);
    openAiMessages.add(new SystemMessage(systemPrompt));

    // 2-2. [대화 흐름 기억] DB에 쌓여있던 이전 대화 역사 가져와서 차례대로 리스트에 추가
    for (ChatHistory history : session.getHistories()) {
      if (history.getRole() == MessageRole.USER) {
        openAiMessages.add(new UserMessage(history.getContent()));
      } else if (history.getRole() == MessageRole.ASSISTANT) {
        openAiMessages.add(new AssistantMessage(history.getContent()));
      }
    }

    // 2-3. 마지막으로 사용자가 방금 입력한 현재 질문 추가
    openAiMessages.add(new UserMessage(question));

    // 3. [OpenAI 호출] Spring AI 정식 API 호출부
    String answer = this.chatClient.prompt()
        .messages(openAiMessages)
        .call()
        .content();

    // 4. 역사 및 세션 정보 영속화 (기존 흐름 유지)
    if (session.getTitle() == null || session.getTitle().isBlank()) {
      session.renameTo(summarizeForTitle(question));
    }
    session.addHistory(ChatHistory.of(MessageRole.USER, question));
    session.addHistory(ChatHistory.of(MessageRole.ASSISTANT, answer));

    return answer;
  }

  private String buildSystemPrompt(String retrievedContext) {
    String persona = "당신은 삼육대학교 학사 안내 지원 챗봇입니다. 학생들에게 친절하게 안내해 주세요.";
    if (retrievedContext == null || retrievedContext.isBlank()) {
      return persona;
    }
    return persona
        + "\n\n다음은 답변에 활용할 대학 내부 공식 자료입니다. 반드시 제공된 자료에 근거하여 답하고, "
        + "자료에 없는 내용이거나 근거가 많이 부족하면 억지로 지어내지 말고 '제공해 드린 지식 파일에 관련 내용이 확인되지 않아 답변이 어렵습니다.'라고 정중히 답하세요.\n\n[참고자료]\n"
        + retrievedContext;
  }

  private String summarizeForTitle(String question) {
    String trimmed = question.strip();
    if (trimmed.length() <= TITLE_MAX_LENGTH) return trimmed;
    return trimmed.substring(0, TITLE_MAX_LENGTH) + "…";
  }

  private void requireLoginId(String loginId) {
    if (loginId == null || loginId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
    }
  }
}