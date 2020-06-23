package com.riatServer.controller;

import com.riatServer.domain.Mark;
import com.riatServer.repo.MarksRepo;
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
@RequestMapping("mark")
public class MarkController {
    private final MarksRepo markRepo;

    @Autowired
    public MarkController(MarksRepo marksRepo)
    {
        this.markRepo = marksRepo;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<Mark>> List(){
        List<Mark> marks = markRepo.findAll();
        if(marks.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(marks, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<Mark> create(@RequestBody Mark  mark){
        if(mark == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        markRepo.save(mark);
        return  new ResponseEntity<>(mark, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<Mark> update(
            @PathVariable("id") Long markId,
            @RequestBody Mark mark
    )
    {
        Mark markFromDb = markRepo.findById(markId).orElse(null);
        if(markFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(mark, markFromDb, "id");
        markRepo.save(markFromDb);
        return new ResponseEntity<>(markFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<Mark> delete(@PathVariable("id") Long markId){
        Mark mark = markRepo.findById(markId).orElse(null);
        if(mark == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        markRepo.delete(mark);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
