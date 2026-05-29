package com.example.syu_ctn_be.chat.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.syu_ctn_be.entity.ChatHistory;
import com.example.syu_ctn_be.entity.ChatSession;
import com.example.syu_ctn_be.entity.MessageRole;
import com.example.syu_ctn_be.entity.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ChatSessionTest {

    private User newUser(String loginId) {
        return User.registerUser(loginId, "password", loginId, loginId + "@example.com", "dept", 1, "010", "STUDENT");
    }

    @Nested
    class OpenFor {

        @Test
        void createsUntitledSession() {
            User user = newUser("alice");

            ChatSession session = ChatSession.openFor(user);

            assertThat(session.getUser()).isSameAs(user);
            assertThat(session.getTitle()).isNull();
            assertThat(session.getCreatedAt()).isNotNull();
            assertThat(session.getHistories()).isEmpty();
        }
    }

    @Nested
    class RenameTo {

        @Test
        void updatesTitle() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            session.renameTo("Curriculum question");

            assertThat(session.getTitle()).isEqualTo("Curriculum question");
        }
    }

    @Nested
    class IsOwnedBy {

        @Test
        void returnsTrueForSameLoginId() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            assertThat(session.isOwnedBy("alice")).isTrue();
        }

        @Test
        void returnsFalseForDifferentLoginId() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            assertThat(session.isOwnedBy("bob")).isFalse();
        }

        @Test
        void returnsFalseForNullLoginId() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            assertThat(session.isOwnedBy(null)).isFalse();
        }
    }

    @Nested
    class AddHistory {

        @Test
        void linksHistoryToSession() {
            ChatSession session = ChatSession.openFor(newUser("alice"));
            ChatHistory history = ChatHistory.of(MessageRole.USER, "hello");

            session.addHistory(history);

            assertThat(session.getHistories()).containsExactly(history);
            assertThat(history.getSession()).isSameAs(session);
        }
    }
}
