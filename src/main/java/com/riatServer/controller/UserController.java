package com.riatServer.controller;

import com.riatServer.domain.Position;
import com.riatServer.domain.User;
import com.riatServer.repo.PositionsRepo;
import com.riatServer.repo.UsersRepo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Api(description = "Операции по взаимодействию с отделами")
@RestController
@RequestMapping("user")
public class UserController {
    private final UsersRepo userRepo;
    private final PositionsRepo positionRepo;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserController(UsersRepo usersRepo, PositionsRepo positionRepo, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userRepo = usersRepo;
        this.positionRepo = positionRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<User>> List(){
        List<User> users = userRepo.findAll();
        if(users.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping("{id}")
    public ResponseEntity<User> List(@PathVariable("id") Long id){
        User user = userRepo.getOne(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User  user){
        if(user == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userRepo.save(user);
        return  new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<User> update(
            @PathVariable("id") Long userId,
            @RequestBody User user
    )
    {
        User userFromDb = userRepo.findById(userId).orElse(null);
        if(userFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(user, userFromDb, "id");
        userRepo.save(userFromDb);
        return new ResponseEntity<>(userFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<User> delete(@PathVariable("id") Long userId){
        User user = userRepo.findById(userId).orElse(null);
        if(user == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userRepo.delete(user);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Регистрация пользователя (необходима для создания учетной записи администратора после установки системы, после должна быть удалена)")
    @PostMapping("/sign-up")
    public void signUp(@RequestBody User user) {
        if(userRepo.findByName(user.getName()) == null){
            Position role = positionRepo.findById(user.getPositionId()).orElse(null);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setPosition_id(role);
            user.setCreateDate(LocalDateTime.now());
            userRepo.save(user);
        }
    }
}
