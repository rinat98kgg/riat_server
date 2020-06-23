package com.riatServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@ToString
@EqualsAndHashCode(of = {"id"})
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(updatable = false, insertable = false, nullable = false, name = "sender_id")
    private long senderId;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender_id;

    @Column(updatable = false, insertable = false, nullable = false, name = "addressee_id")
    private long addresseeId;

    @ManyToOne
    @JoinColumn(name = "addressee_id")
    private User addressee_id;

    @Column(updatable = true, nullable = true)
    private String text;

    @Column(updatable = true, nullable = true)
    private boolean readOrNo;

    @Column(updatable = false, nullable = false, name = "create_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    //getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public User getSender_id() {
        return sender_id;
    }

    public void setSender_id(User sender_id) {
        this.sender_id = sender_id;
    }

    public long getAddresseeId() {
        return addresseeId;
    }

    public void setAddresseeId(long addresseeId) {
        this.addresseeId = addresseeId;
    }

    public User getAddressee_id() {
        return addressee_id;
    }

    public void setAddressee_id(User addressee_id) {
        this.addressee_id = addressee_id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isReadOrNo() {
        return readOrNo;
    }

    public void setReadOrNo(boolean readOrNo) {
        this.readOrNo = readOrNo;
    }
}