package com.riatServer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.riatServer.domain.Message;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDto {
    String text;
    long adresseeId;
    long userId;
    boolean haveunreadmessages;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime lastSeenTime;
    long unreadmessages;

    public MessageDto toMessage(){
        MessageDto messageDto = new MessageDto();
        messageDto.setAdresseeId(adresseeId);
        messageDto.setHaveunreadmessages(haveunreadmessages);
        messageDto.setUserId(userId);
        messageDto.setText(text);
        return messageDto;
    }

    public static MessageDto fromMessage(List<Message> message){
        MessageDto messageDto = new MessageDto();
        if(message.size()==0){
            messageDto.setText("");
            messageDto.setHaveunreadmessages(false);
            messageDto.setUnreadmessages(0);

        }
        else {
            int count =0;
            for(int i=0;i<message.size();i++){
                if(message.get(i).isReadOrNo()){
                    count++;
                }
            }
            int lastSize = message.size() - 1;
            messageDto.setAdresseeId(message.get(lastSize).getAddresseeId());
            messageDto.setUserId(message.get(lastSize).getSenderId());
            messageDto.setText(message.get(lastSize).getText());
            messageDto.setHaveunreadmessages(message.get(lastSize).isReadOrNo());
            messageDto.setUnreadmessages(count);
            messageDto.setLastSeenTime(message.get(lastSize).getUpdateDate());
        }
        return messageDto;
    }
}