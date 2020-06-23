package com.riatServer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.riatServer.domain.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageFromChatDto {
    String text;
    long senderId;
    long addresseeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime sendDate;

    public MessageFromChatDto toMessageFromChat(){
        MessageFromChatDto message = new MessageFromChatDto();
        message.setAddresseeId(addresseeId);
        message.setSendDate(sendDate);
        message.setSenderId(senderId);
        message.setText(text);
        return message;
    }

    public static MessageFromChatDto fromChatDto(Message message){
        MessageFromChatDto messageFromChatDto = new MessageFromChatDto();
        messageFromChatDto.setText(message.getText());
        messageFromChatDto.setSenderId(message.getSenderId());
        messageFromChatDto.setSendDate(message.getCreateDate());
        messageFromChatDto.setAddresseeId(message.getAddresseeId());
        return messageFromChatDto;
    }
}