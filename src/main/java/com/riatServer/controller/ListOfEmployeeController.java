
package com.riatServer.controller;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.TaskStatus;
import com.riatServer.domain.User;
import com.riatServer.dto.EmployeeTaskDto;
import com.riatServer.dto.StatisticDto;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.DepartmentStaffsRepo;
import com.riatServer.repo.TaskStatusRepo;
import com.riatServer.repo.UsersRepo;
import com.riatServer.service.Impl.ListOfEmployeeServiceImpl;
import com.riatServer.service.Impl.TaskServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api(description = "Операции по взаимодействию с отделами")
@RestController
@RequestMapping("listOfEmployee")
public class ListOfEmployeeController {
    private final ListOfEmployeeServiceImpl listOfEmployeeService;
    private final TaskServiceImpl taskService;
    private final DepartmentStaffsRepo departmentStaffsRepo;
    private  final TaskStatusRepo taskStatus;
    private final UsersRepo userService;

    @Autowired
    public ListOfEmployeeController(ListOfEmployeeServiceImpl listOfEmployeeService, TaskServiceImpl taskService, DepartmentStaffsRepo departmentStaffsRepo, TaskStatusRepo taskStatus, UsersRepo userService)
    {
        this.listOfEmployeeService = listOfEmployeeService;
        this.taskService = taskService;
        this.departmentStaffsRepo = departmentStaffsRepo;
        this.taskStatus = taskStatus;
        this.userService = userService;
    }

    @ApiOperation(value = "Получения списка всех задач пользователя")
    @GetMapping("{userId}/{taskId}")
    public ResponseEntity<EmployeeTaskDto> List(@PathVariable("userId") Long userId, @PathVariable("taskId") Long taskId){
        ListOfEmployees user = listOfEmployeeService.taskInfo(userId, true, taskId);
        Task taskTemp = taskService.getById(taskId);
        User tempUser = userService.getOne(user.getOwnerId());
        EmployeeTaskDto task = EmployeeTaskDto.fromUser(user, taskTemp, tempUser);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @ApiOperation(value = "Получения списка всех задач для статистики")
    @GetMapping("{userId}")
    public ResponseEntity<List<StatisticDto>> List(@PathVariable("userId") long userId){
        List<ListOfEmployees> listOfEmployees = listOfEmployeeService.statistic(userId);
        List<StatisticDto> statisticDtos = new ArrayList<>();
        TaskStatus status = new TaskStatus();
        long id;
        for(int i =0;i<listOfEmployees.size();i++){
            id = listOfEmployees.get(i).getTaskStatusId();
            status = taskStatus.getOne(id);
            statisticDtos.add(StatisticDto.fromStatisticDto(listOfEmployees.get(i).getUpdateDate(),status.getName()));
        }
        if(listOfEmployees.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(statisticDtos, HttpStatus.OK);
    }

    @ApiOperation(value = "Получения списка всех задач для статистики")
    @GetMapping("{departmentId}/all")
    public ResponseEntity<List<StatisticDto>> ListAll(@PathVariable("departmentId") long departmentId){
        Long depId = departmentStaffsRepo.userDep(departmentId);
        List<Long> allUserId = departmentStaffsRepo.userAllDep(depId);
        List<ListOfEmployees> listOfEmployees = new ArrayList<>();
        for(int i =0;i<allUserId.size();i++) {
            listOfEmployees.addAll(listOfEmployeeService.statistic(allUserId.get(i)));
        }
        List<StatisticDto> statisticDtos = new ArrayList<>();
        TaskStatus status = new TaskStatus();
        long id;
        for(int i =0;i<listOfEmployees.size();i++){
            id = listOfEmployees.get(i).getTaskStatusId();
            status = taskStatus.getOne(id);
            statisticDtos.add(StatisticDto.fromStatisticDto(listOfEmployees.get(i).getUpdateDate(),status.getName()));
        }
        if(listOfEmployees.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(statisticDtos, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<ListOfEmployees> create(@RequestBody ListOfEmployees  listOfEmployee){
        if(listOfEmployee == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        listOfEmployeeService.create(listOfEmployee);
        return  new ResponseEntity<>(listOfEmployee, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<ListOfEmployees> update(
            @PathVariable("id") Long listOfEmployeeId,
            @RequestBody ListOfEmployees listOfEmployee
    )
    {
        ListOfEmployees listOfEmployeeFromDb = listOfEmployeeService.getById(listOfEmployeeId);
        if(listOfEmployeeFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(listOfEmployee, listOfEmployeeFromDb, "id");
        listOfEmployeeService.save(listOfEmployeeFromDb);
        return new ResponseEntity<>(listOfEmployeeFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<ListOfEmployees> delete(@PathVariable("id") Long listOfEmployeeId) throws IOException, ServiceException {
        listOfEmployeeService.delete(listOfEmployeeId);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}