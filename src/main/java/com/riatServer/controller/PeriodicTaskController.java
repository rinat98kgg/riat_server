package com.riatServer.controller;

import com.riatServer.domain.PeriodicTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.riatServer.repo.PeriodicTasksRepo;

import java.util.List;

@Api(description = "Операции по взаимодействию с отделами")
@RestController
@RequestMapping("periodicTask")
public class PeriodicTaskController {
    private final PeriodicTasksRepo periodicTaskRepo;

    @Autowired
    public PeriodicTaskController(PeriodicTasksRepo periodicTasksRepo)
    {
        this.periodicTaskRepo = periodicTasksRepo;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<PeriodicTask>> List(){
        List<PeriodicTask> periodicTasks = periodicTaskRepo.findAll();
        if(periodicTasks.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(periodicTasks, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<PeriodicTask> create(@RequestBody PeriodicTask  periodicTask){
        if(periodicTask == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        periodicTaskRepo.save(periodicTask);
        return  new ResponseEntity<>(periodicTask, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<PeriodicTask> update(
            @PathVariable("id") Long periodicTaskId,
            @RequestBody PeriodicTask periodicTask
    )
    {
        PeriodicTask periodicTaskFromDb = periodicTaskRepo.findById(periodicTaskId).orElse(null);
        if(periodicTaskFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(periodicTask, periodicTaskFromDb, "id");
        periodicTaskRepo.save(periodicTaskFromDb);
        return new ResponseEntity<>(periodicTaskFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<PeriodicTask> delete(@PathVariable("id") Long periodicTaskId){
        PeriodicTask periodicTask = periodicTaskRepo.findById(periodicTaskId).orElse(null);
        if(periodicTask == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        periodicTaskRepo.delete(periodicTask);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
