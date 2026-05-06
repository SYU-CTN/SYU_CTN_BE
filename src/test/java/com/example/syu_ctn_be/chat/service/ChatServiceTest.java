package com.example.syu_ctn_be.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.syu_ctn_be.chat.config.OpenAiProperties;
import com.example.syu_ctn_be.chat.entity.ChatSession;
import com.example.syu_ctn_be.chat.entity.MessageRole;
import com.example.syu_ctn_be.chat.repository.ChatSessionRepository;
import com.example.syu_ctn_be.chat.repository.DocumentRepository;
import com.example.syu_ctn_be.domain.User;
import com.example.syu_ctn_be.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

/**
 * ChatService 단위 테스트.
 * 외부 의존성(Repository, OpenAI 클라이언트, RAG)은 모두 모킹한다.
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatSessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private OpenAiClient openAiClient;
    @Mock private OpenAiProperties openAiProperties;

    @InjectMocks private ChatService chatService;

    private static final String LOGIN_ID = "alice";

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().loginId(LOGIN_ID).build();
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Nested
    @DisplayName("startSession")
    class StartSession {

        @Test
        void 빈_loginId_는_401_을_던진다() {
            assertThatThrownBy(() -> chatService.startSession("  "))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(sessionRepository, never()).save(any());
        }

        @Test
        void USERS_에_없는_loginId_는_401_을_던진다() {
            when(userRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatService.startSession(LOGIN_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(sessionRepository, never()).save(any());
        }

        @Test
        void 새_세션을_저장하고_생성된_id_를_반환한다() {
            ChatSession created = ChatSession.openFor(user);
            ReflectionTestUtils.setField(created, "id", 42L);

            when(userRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(user));
            when(sessionRepository.findAllByUser_LoginIdOrderByCreatedAtDesc(LOGIN_ID)).thenReturn(List.of());
            when(sessionRepository.save(any(ChatSession.class))).thenReturn(created);

            Long newSessionId = chatService.startSession(LOGIN_ID);

            assertThat(newSessionId).isEqualTo(42L);
            verify(sessionRepository, never()).deleteAll(anyList());
            verify(sessionRepository, times(1)).save(any(ChatSession.class));
        }

        @Test
        void 기존_세션이_있으면_모두_폐기하고_새로_만든다() {
            ChatSession previous = ChatSession.openFor(user);
            ReflectionTestUtils.setField(previous, "id", 9L);
            ChatSession created = ChatSession.openFor(user);
            ReflectionTestUtils.setField(created, "id", 100L);

            when(userRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(user));
            when(sessionRepository.findAllByUser_LoginIdOrderByCreatedAtDesc(LOGIN_ID))
                    .thenReturn(List.of(previous));
            when(sessionRepository.save(any(ChatSession.class))).thenReturn(created);

            Long newSessionId = chatService.startSession(LOGIN_ID);

            assertThat(newSessionId).isEqualTo(100L);
            verify(sessionRepository, times(1)).deleteAll(List.of(previous));
            verify(sessionRepository, times(1)).save(any(ChatSession.class));
        }
    }

    @Nested
    @DisplayName("discardSession")
    class DiscardSession {

        private static final Long SESSION_ID = 11L;

        @Test
        void 빈_loginId_는_401_을_던진다() {
            assertThatThrownBy(() -> chatService.discardSession(" ", SESSION_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(sessionRepository, never()).delete(any());
        }

        @Test
        void sessionId_가_null_이면_400_을_던진다() {
            assertThatThrownBy(() -> chatService.discardSession(LOGIN_ID, null))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

            verify(sessionRepository, never()).delete(any());
        }

        @Test
        void 존재하지_않는_세션은_404_를_던진다() {
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatService.discardSession(LOGIN_ID, SESSION_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

            verify(sessionRepository, never()).delete(any());
        }

        @Test
        void 다른_사용자의_세션은_403_을_던진다() {
            User other = User.builder().loginId("bob").build();
            ChatSession bobSession = ChatSession.openFor(other);
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(bobSession));

            assertThatThrownBy(() -> chatService.discardSession(LOGIN_ID, SESSION_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));

            verify(sessionRepository, never()).delete(any());
        }

        @Test
        void 본인_세션은_즉시_삭제된다() {
            ChatSession session = ChatSession.openFor(user);
            ReflectionTestUtils.setField(session, "id", SESSION_ID);
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

            chatService.discardSession(LOGIN_ID, SESSION_ID);

            verify(sessionRepository, times(1)).delete(session);
        }
    }

    @Nested
    @DisplayName("ask")
    class Ask {

        private static final Long SESSION_ID = 7L;

        private ChatSession session;

        @BeforeEach
        void setUpSession() {
            session = ChatSession.openFor(user);
            ReflectionTestUtils.setField(session, "id", SESSION_ID);
        }

        @Test
        void 빈_loginId_는_401_을_던진다() {
            assertThatThrownBy(() -> chatService.ask("", SESSION_ID, "질문?"))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
        }

        @Test
        void sessionId_가_null_이면_400_을_던진다() {
            assertThatThrownBy(() -> chatService.ask(LOGIN_ID, null, "질문?"))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        }

        @Test
        void 질문이_비어있으면_400_을_던진다() {
            assertThatThrownBy(() -> chatService.ask(LOGIN_ID, SESSION_ID, "   "))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        }

        @Test
        void 존재하지_않는_세션은_404_를_던진다() {
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatService.ask(LOGIN_ID, SESSION_ID, "질문?"))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        void 다른_사용자의_세션은_403_을_던진다() {
            User other = User.builder().loginId("bob").build();
            ChatSession bobSession = ChatSession.openFor(other);
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(bobSession));

            assertThatThrownBy(() -> chatService.ask(LOGIN_ID, SESSION_ID, "질문?"))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));

            verify(openAiClient, never()).complete(anyList());
        }

        @Test
        void 첫_질문이면_제목으로_요약되고_USER_와_ASSISTANT_2건이_누적된다() {
            String question = "커리큘럼 알려줘";
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
            when(documentRepository.findRelevantContext(question)).thenReturn("- 1학년 기초 SW");
            when(openAiProperties.getSystemPersona()).thenReturn("당신은 도우미입니다.");
            when(openAiClient.complete(anyList())).thenReturn("도움이 될 답변입니다.");

            String answer = chatService.ask(LOGIN_ID, SESSION_ID, question);

            assertThat(answer).isEqualTo("도움이 될 답변입니다.");
            assertThat(session.getTitle()).isEqualTo(question);
            assertThat(session.getHistories()).hasSize(2);
            assertThat(session.getHistories().get(0).getRole()).isEqualTo(MessageRole.USER);
            assertThat(session.getHistories().get(0).getContent()).isEqualTo(question);
            assertThat(session.getHistories().get(1).getRole()).isEqualTo(MessageRole.ASSISTANT);
            assertThat(session.getHistories().get(1).getContent()).isEqualTo("도움이 될 답변입니다.");
            verify(openAiClient, times(1)).complete(anyList());
        }

        @Test
        void 이미_제목이_설정된_세션은_제목이_덮어쓰이지_않는다() {
            session.renameTo("기존 제목");
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
            when(documentRepository.findRelevantContext(any())).thenReturn("");
            when(openAiProperties.getSystemPersona()).thenReturn("당신은 도우미입니다.");
            when(openAiClient.complete(anyList())).thenReturn("응답");

            chatService.ask(LOGIN_ID, SESSION_ID, "두 번째 질문");

            assertThat(session.getTitle()).isEqualTo("기존 제목");
        }
    }
}
