package com.riatServer.controller;

import com.riatServer.domain.Position;
import com.riatServer.repo.PositionsRepo;
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
@RequestMapping("position")
public class PositionController {
    private final PositionsRepo positionRepo;

    @Autowired
    public PositionController(PositionsRepo positionsRepo)
    {
        this.positionRepo = positionsRepo;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<Position>> List(){
        List<Position> positions = positionRepo.findAll();
        if(positions.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(positions, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<Position> create(@RequestBody Position position){
        if(position == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        positionRepo.save(position);
        return  new ResponseEntity<>(position, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<Position> update(
            @PathVariable("id") Long positionId,
            @RequestBody Position position
    )
    {
        Position positionFromDb = positionRepo.findById(positionId).orElse(null);
        if(positionFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(position, positionFromDb, "id");
        positionRepo.save(positionFromDb);
        return new ResponseEntity<>(positionFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<Position> delete(@PathVariable("id") Long positionId){
        Position position = positionRepo.findById(positionId).orElse(null);
        if(position == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        positionRepo.delete(position);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
