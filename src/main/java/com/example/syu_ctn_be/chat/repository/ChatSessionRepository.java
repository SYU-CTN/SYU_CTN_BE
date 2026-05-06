package com.example.syu_ctn_be.chat.repository;

import com.example.syu_ctn_be.chat.entity.ChatSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findAllByUser_LoginIdOrderByCreatedAtDesc(String loginId);
}
