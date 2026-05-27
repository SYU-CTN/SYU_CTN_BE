package com.example.syu_ctn_be.entity;

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

@Entity
@Getter
@Table(name = "SESSIONS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", referencedColumnName = "LOGIN_ID", nullable = false)
    private User user;

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

    public void renameTo(String title) {
        this.title = title;
    }

    public void addHistory(ChatHistory history) {
        this.histories.add(history);
        history.linkTo(this);
    }

    public boolean isOwnedBy(String loginId) {
        return this.user != null && loginId != null && loginId.equals(this.user.getLoginId());
    }
}
