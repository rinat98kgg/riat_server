package com.riatServer.controller;

import com.riatServer.domain.ListOfTask;
import com.riatServer.repo.TasksRepo;
import com.riatServer.service.Impl.TaskServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.riatServer.repo.ListOfTasksRepo;

import java.time.LocalDateTime;
import java.util.List;

@Api(description = "Операции по взаимодействию с отделами")
@RestController
@RequestMapping("listOfTask")
public class ListOfTaskController {
    private final ListOfTasksRepo listOfTaskRepo;
    private final TaskServiceImpl tasksRepo;

    @Autowired
    public ListOfTaskController(ListOfTasksRepo listOfTasksRepo, TaskServiceImpl tasksRepo)
    {
        this.listOfTaskRepo = listOfTasksRepo;
        this.tasksRepo = tasksRepo;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<ListOfTask>> List(){
        List<ListOfTask> listOfTasks = listOfTaskRepo.findAll();
        if(listOfTasks.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listOfTasks, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping("{id}/{taskId}")
    public ResponseEntity<ListOfTask> create(@PathVariable("id") Long top, @PathVariable("taskId") Long subtaskId){
        ListOfTask task = new ListOfTask();
        task.setTopId(top);
        task.setCreateDate(LocalDateTime.now());
        task.setSubtaskId(subtaskId);
        task.setUpdateDate(LocalDateTime.now());
        task.setSubtask(tasksRepo.getById(subtaskId));
        task.setTopTask(tasksRepo.getById(top));
        listOfTaskRepo.save(task);
        return  new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<ListOfTask> update(
            @PathVariable("id") Long listOfTaskId,
            @RequestBody ListOfTask listOfTask
    )
    {
        ListOfTask listOfTaskFromDb = listOfTaskRepo.findById(listOfTaskId).orElse(null);
        if(listOfTaskFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(listOfTask, listOfTaskFromDb, "id");
        listOfTaskRepo.save(listOfTaskFromDb);
        return new ResponseEntity<>(listOfTaskFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<ListOfTask> delete(@PathVariable("id") Long listOfTaskId){
        ListOfTask listOfTask = listOfTaskRepo.findById(listOfTaskId).orElse(null);
        if(listOfTask == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        listOfTaskRepo.delete(listOfTask);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}