package com.example.syu_ctn_be.chat.entity;

import com.example.syu_ctn_be.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자별 대화방. SESSIONS 테이블 매핑.
 * USER_ID 컬럼은 USERS.LOGIN_ID 를 참조하는 외래키로 동작한다.
 */
@Entity
@Getter
@Table(name = "SESSIONS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 대화 주체. 외래키는 USERS.LOGIN_ID 를 참조한다. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", referencedColumnName = "LOGIN_ID", nullable = false)
    private User user;

    /** 대화방 제목(첫 질문 요약 등). 생성 직후에는 비어 있을 수 있다. */
    @Column(name = "TITLE", length = 255)
    private String title;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatHistory> histories = new ArrayList<>();

    @Builder
    private ChatSession(User user, String title) {
        this.user = user;
        this.title = title;
        this.createdAt = LocalDateTime.now();
    }

    public static ChatSession openFor(User user) {
        return ChatSession.builder().user(user).build();
    }

    /** 첫 질문 요약 등으로 대화방 제목을 갱신한다. Setter 대신 사용한다. */
    public void renameTo(String title) {
        this.title = title;
    }

    /** 양방향 연관관계 편의 메서드. */
    public void addHistory(ChatHistory history) {
        this.histories.add(history);
        history.linkTo(this);
    }

    /** 컨트롤러/서비스에서 세션 소유자 검증에 사용한다. */
    public boolean isOwnedBy(String loginId) {
        return this.user != null && loginId != null && loginId.equals(this.user.getLoginId());
    }
}
