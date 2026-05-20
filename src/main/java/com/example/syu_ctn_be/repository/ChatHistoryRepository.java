package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
}
