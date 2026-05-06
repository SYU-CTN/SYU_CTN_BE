package com.example.syu_ctn_be.chat.repository;

import com.example.syu_ctn_be.chat.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
}
