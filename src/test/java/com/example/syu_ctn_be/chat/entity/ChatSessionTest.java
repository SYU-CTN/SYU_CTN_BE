package com.example.syu_ctn_be.chat.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.syu_ctn_be.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ChatSession 엔티티의 비즈니스 메서드 단위 테스트.
 * Spring 컨테이너 없이 순수 객체 동작만 검증한다.
 */
class ChatSessionTest {

    private User newUser(String loginId) {
        return User.builder().loginId(loginId).build();
    }

    @Nested
    @DisplayName("openFor")
    class OpenFor {

        @Test
        void 새_세션은_제목없이_생성되고_생성시각이_기록된다() {
            User user = newUser("alice");

            ChatSession session = ChatSession.openFor(user);

            assertThat(session.getUser()).isSameAs(user);
            assertThat(session.getTitle()).isNull();
            assertThat(session.getCreatedAt()).isNotNull();
            assertThat(session.getHistories()).isEmpty();
        }
    }

    @Nested
    @DisplayName("renameTo")
    class RenameTo {

        @Test
        void 제목을_갱신하면_새_제목이_반영된다() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            session.renameTo("커리큘럼 관련 질문");

            assertThat(session.getTitle()).isEqualTo("커리큘럼 관련 질문");
        }
    }

    @Nested
    @DisplayName("isOwnedBy")
    class IsOwnedBy {

        @Test
        void 같은_loginId_요청자는_소유자로_인정된다() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            assertThat(session.isOwnedBy("alice")).isTrue();
        }

        @Test
        void 다른_loginId_는_소유자가_아니다() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            assertThat(session.isOwnedBy("bob")).isFalse();
        }

        @Test
        void null_loginId_는_소유자가_아니다() {
            ChatSession session = ChatSession.openFor(newUser("alice"));

            assertThat(session.isOwnedBy(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("addHistory")
    class AddHistory {

        @Test
        void 메시지를_추가하면_세션과_양방향으로_연결된다() {
            ChatSession session = ChatSession.openFor(newUser("alice"));
            ChatHistory history = ChatHistory.of(MessageRole.USER, "안녕하세요");

            session.addHistory(history);

            assertThat(session.getHistories()).containsExactly(history);
            assertThat(history.getSession()).isSameAs(session);
        }
    }
}
