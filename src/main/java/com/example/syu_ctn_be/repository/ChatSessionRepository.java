package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.entity.ChatSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findAllByUser_LoginIdOrderByCreatedAtDesc(String loginId);
}
