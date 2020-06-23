package com.riatServer.service.Impl;

import com.riatServer.domain.Message;
import com.riatServer.repo.MessagesRepo;
import com.riatServer.repo.UsersRepo;
import com.riatServer.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl  implements MessageService {
    @Autowired
    MessagesRepo messagesRepo;
    @Autowired
    UsersRepo usersRepo;

    public List<Message> getAll(List<Long> userId) {
        List<Message> message = messagesRepo.findAll();
        List<Message> tempMsg = new ArrayList<>();
        for(int i = 0;i<message.size();i++){
            for(int j =0;j<userId.size();j++){
                if(message.get(i).getAddresseeId()==userId.get(j)){
                    tempMsg.add(message.get(i));
                }
            }
        }
        return tempMsg;
    }

    public Message save(Message message) {
        message.setUpdateDate(LocalDateTime.now());
        return messagesRepo.save(message);
    }

    public Message create(Message message) {
        message.setCreateDate(LocalDateTime.now());
        message.setUpdateDate(LocalDateTime.now());
        message.setAddressee_id(usersRepo.findById(message.getAddresseeId()).orElse(null));
        message.setSender_id(usersRepo.findById(message.getSenderId()).orElse(null));
        System.out.println(message.getText());
        System.out.println(message.isReadOrNo());
        return messagesRepo.save(message);
    }

    public Message getById(Long id) {
        return null;
    }




}