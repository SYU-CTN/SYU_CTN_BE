package com.example.syu_ctn_be.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대화방 내부의 단일 메시지 이력. HISTORY 테이블 매핑.
 * 발신자는 USER 또는 ASSISTANT 둘 중 하나로 구분한다.
 */
@Entity
@Getter
@Table(name = "HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SESSION_ID", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private MessageRole role;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private ChatHistory(MessageRole role, String content) {
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public static ChatHistory of(MessageRole role, String content) {
        return ChatHistory.builder().role(role).content(content).build();
    }

    /** 양방향 연관관계 편의 메서드(ChatSession.addHistory 에서만 호출). */
    void linkTo(ChatSession session) {
        this.session = session;
    }
}
