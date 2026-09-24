package com.assistant.scheme.repository;

import com.assistant.scheme.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ChatHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);
    List<ChatHistory> findByUserIdAndSessionIdOrderByCreatedAtAsc(Long userId, String sessionId);
}
