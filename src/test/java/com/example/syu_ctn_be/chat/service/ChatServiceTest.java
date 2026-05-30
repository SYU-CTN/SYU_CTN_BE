package com.example.syu_ctn_be.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.syu_ctn_be.config.OpenAiProperties;
import com.example.syu_ctn_be.entity.ChatSession;
import com.example.syu_ctn_be.entity.MessageRole;
import com.example.syu_ctn_be.entity.User;
import com.example.syu_ctn_be.repository.ChatSessionRepository;
import com.example.syu_ctn_be.repository.DocumentRepository;
import com.example.syu_ctn_be.repository.UserRepository;
import com.example.syu_ctn_be.service.ChatService;
import com.example.syu_ctn_be.service.OpenAiClient;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    private static final String LOGIN_ID = "alice";

    @Mock private ChatSessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private OpenAiClient openAiClient;
    @Mock private OpenAiProperties openAiProperties;

    @InjectMocks private ChatService chatService;

    private User user;

    @BeforeEach
    void setUp() {
        user = newUser(LOGIN_ID);
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    private User newUser(String loginId) {
        return User.registerUser(loginId, "password", loginId, loginId + "@example.com", "dept", 1, "010", "STUDENT");
    }

    @Nested
    class StartSession {

        @Test
        void rejectsBlankLoginId() {
            assertThatThrownBy(() -> chatService.startSession("  "))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(sessionRepository, never()).save(any());
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatService.startSession(LOGIN_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(sessionRepository, never()).save(any());
        }

        @Test
        void createsSession() {
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
        void deletesExistingSessionsBeforeCreatingNewOne() {
            ChatSession previous = ChatSession.openFor(user);
            ChatSession created = ChatSession.openFor(user);
            ReflectionTestUtils.setField(created, "id", 100L);

            when(userRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(user));
            when(sessionRepository.findAllByUser_LoginIdOrderByCreatedAtDesc(LOGIN_ID))
                    .thenReturn(List.of(previous));
            when(sessionRepository.save(any(ChatSession.class))).thenReturn(created);

            Long newSessionId = chatService.startSession(LOGIN_ID);

            assertThat(newSessionId).isEqualTo(100L);
            verify(sessionRepository, times(1)).deleteAll(List.of(previous));
        }
    }

    @Nested
    class DiscardSession {

        private static final Long SESSION_ID = 11L;

        @Test
        void rejectsMissingSessionId() {
            assertThatThrownBy(() -> chatService.discardSession(LOGIN_ID, null))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

            verify(sessionRepository, never()).delete(any());
        }

        @Test
        void rejectsUnknownSession() {
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatService.discardSession(LOGIN_ID, SESSION_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        void rejectsSessionOwnedByAnotherUser() {
            User other = newUser("bob");
            ChatSession bobSession = ChatSession.openFor(other);
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(bobSession));

            assertThatThrownBy(() -> chatService.discardSession(LOGIN_ID, SESSION_ID))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));

            verify(sessionRepository, never()).delete(any());
        }

        @Test
        void deletesOwnedSession() {
            ChatSession session = ChatSession.openFor(user);
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

            chatService.discardSession(LOGIN_ID, SESSION_ID);

            verify(sessionRepository, times(1)).delete(session);
        }
    }

    @Nested
    class Ask {

        private static final Long SESSION_ID = 7L;

        private ChatSession session;

        @BeforeEach
        void setUpSession() {
            session = ChatSession.openFor(user);
            ReflectionTestUtils.setField(session, "id", SESSION_ID);
        }

        @Test
        void rejectsBlankQuestion() {
            assertThatThrownBy(() -> chatService.ask(LOGIN_ID, SESSION_ID, "   "))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        }

        @Test
        void rejectsSessionOwnedByAnotherUser() {
            User other = newUser("bob");
            ChatSession bobSession = ChatSession.openFor(other);
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(bobSession));

            assertThatThrownBy(() -> chatService.ask(LOGIN_ID, SESSION_ID, "question"))
                    .isInstanceOfSatisfying(ResponseStatusException.class,
                            ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));

            verify(openAiClient, never()).complete(anyList());
        }

        @Test
        void storesUserAndAssistantHistoryWithoutGeneratingTitle() {
            String question = "Tell me about the curriculum";
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
            when(documentRepository.findRelevantContext(question)).thenReturn("- First year basic software");
            when(openAiProperties.getSystemPersona()).thenReturn("You are an academic assistant.");
            when(openAiClient.complete(anyList())).thenReturn("Here is the answer.");

            String answer = chatService.ask(LOGIN_ID, SESSION_ID, question);

            assertThat(answer).isEqualTo("Here is the answer.");
            assertThat(session.getTitle()).isNull();
            assertThat(session.getHistories()).hasSize(2);
            assertThat(session.getHistories().get(0).getRole()).isEqualTo(MessageRole.USER);
            assertThat(session.getHistories().get(0).getContent()).isEqualTo(question);
            assertThat(session.getHistories().get(1).getRole()).isEqualTo(MessageRole.ASSISTANT);
            assertThat(session.getHistories().get(1).getContent()).isEqualTo("Here is the answer.");
            verify(openAiClient, times(1)).complete(anyList());
        }

        @Test
        void keepsExistingTitle() {
            session.renameTo("Existing title");
            when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
            when(documentRepository.findRelevantContext(any())).thenReturn("");
            when(openAiProperties.getSystemPersona()).thenReturn("You are an academic assistant.");
            when(openAiClient.complete(anyList())).thenReturn("Answer");

            chatService.ask(LOGIN_ID, SESSION_ID, "Second question");

            assertThat(session.getTitle()).isEqualTo("Existing title");
        }
    }
}
