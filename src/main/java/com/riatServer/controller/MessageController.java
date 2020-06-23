package com.riatServer.controller;

import com.riatServer.domain.Message;
import com.riatServer.dto.MessageFromChatDto;
import com.riatServer.repo.MessagesRepo;
import com.riatServer.service.Impl.MessageServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(description = "Операции по взаимодействию с сообщениями")
@RestController
@RequestMapping("message")
public class MessageController  {
    private final MessagesRepo messagesRepo;
    private final MessageServiceImpl messageService;

    public MessageController(MessagesRepo messagesRepo, MessageServiceImpl messageService) {
        this.messagesRepo = messagesRepo;
        this.messageService = messageService;
    }

    @ApiOperation(value = "Получения списка всех сообщений чата")
    @GetMapping("{firstUserId}/{secondUserId}")
    public ResponseEntity<List<MessageFromChatDto>> List(@PathVariable("firstUserId") Long firstUserId, @PathVariable("secondUserId") Long secondUserId){
        List<Message> message = messagesRepo.chatMsg(firstUserId, secondUserId);
        List<MessageFromChatDto> messageFromChatDtos = new ArrayList<>();
        for(int i =0;i<message.size();i++){
            messageFromChatDtos.add(MessageFromChatDto.fromChatDto(message.get(i)));
        }
        return new ResponseEntity<>(messageFromChatDtos, HttpStatus.OK);
    }

    @ApiOperation(value = "Получения списка всех сообщений")
    @GetMapping
    public ResponseEntity<List<Message>> List(){
        List<Message> departmentStaffs = messagesRepo.findAll();
        if(departmentStaffs.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(departmentStaffs, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<MessageFromChatDto> create(@RequestBody Message  message){
        if(message == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        MessageFromChatDto messageFromChatDto = MessageFromChatDto.fromChatDto(messageService.create(message));
        return  new ResponseEntity<>(messageFromChatDto, HttpStatus.CREATED);
    }
}