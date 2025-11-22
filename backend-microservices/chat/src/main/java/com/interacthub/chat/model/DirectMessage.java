package com.interacthub.chat.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name="direct_messages", indexes = {
    @Index(name="idx_dm_room", columnList="roomId")
})
public class DirectMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String roomId; // normalized "alice|bob"
    @Column(nullable=false) private String senderName;
    @Column(nullable=false) private String recipientName;
    @Column(nullable=false, length=5000) private String content;
    @Column(nullable=false) private Instant sentAt = Instant.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getRoomId() { return roomId; } public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getSenderName() { return senderName; } public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getRecipientName() { return recipientName; } public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public Instant getSentAt() { return sentAt; } public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
