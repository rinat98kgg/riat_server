package com.riatServer.controller;

import com.riatServer.domain.TaskStatus;
import com.riatServer.repo.TaskStatusRepo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "Операции по взаимодействию с отделами")
@RestController
@RequestMapping("taskStatus")
public class TaskStatusController {
    private final TaskStatusRepo taskStatusRepo;

    @Autowired
    public TaskStatusController(TaskStatusRepo taskStatusRepo)
    {
        this.taskStatusRepo = taskStatusRepo;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<TaskStatus>> List(){
        List<TaskStatus> taskStatus = taskStatusRepo.findAll();
        if(taskStatus.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(taskStatus, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<TaskStatus> create(@RequestBody TaskStatus  taskStatus){
        if(taskStatus == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        taskStatusRepo.save(taskStatus);
        return  new ResponseEntity<>(taskStatus, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<TaskStatus> update(
            @PathVariable("id") Long taskStatusId,
            @RequestBody TaskStatus taskStatus
    )
    {
        TaskStatus taskStatusFromDb = taskStatusRepo.findById(taskStatusId).orElse(null);
        if(taskStatusFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(taskStatus, taskStatusFromDb, "id");
        taskStatusRepo.save(taskStatusFromDb);
        return new ResponseEntity<>(taskStatusFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<TaskStatus> delete(@PathVariable("id") Long taskStatusId){
        TaskStatus taskStatus = taskStatusRepo.findById(taskStatusId).orElse(null);
        if(taskStatus == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        taskStatusRepo.delete(taskStatus);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
