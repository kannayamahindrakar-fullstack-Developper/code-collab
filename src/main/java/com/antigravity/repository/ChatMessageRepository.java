package com.antigravity.repository;

import com.antigravity.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Find all messages between two users in a specific room, ordered by time
    @Query("SELECT m FROM ChatMessage m WHERE m.roomCode = :roomCode AND " +
           "((m.senderUsername = :user1 AND m.receiverUsername = :user2) OR " +
           "(m.senderUsername = :user2 AND m.receiverUsername = :user1)) " +
           "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatHistory(@Param("roomCode") String roomCode, 
                                      @Param("user1") String user1, 
                                      @Param("user2") String user2);

    // Delete all messages in a room
    void deleteByRoomCode(String roomCode);
}
