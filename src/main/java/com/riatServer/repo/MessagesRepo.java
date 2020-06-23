package com.riatServer.repo;

import com.riatServer.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessagesRepo extends JpaRepository<Message, Long> {
    @Query("select u from Message u where (u.addresseeId = ?1 and u.senderId=?2) or (u.addresseeId=?2 and u.senderId=?1)")
    List<Message> chatMsg(Long addresseeId, long senderId);

    @Query("select u from Message u where u.addresseeId = ?1")
    List<Message> userMsg(Long addresseeId);
}