        package com.riatServer.controller;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.User;
import com.riatServer.dto.EmployeeTaskDto;
import com.riatServer.dto.TaskSaveDto;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.UsersRepo;
import com.riatServer.service.Impl.ListOfEmployeeServiceImpl;
import com.riatServer.service.Impl.TaskServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

        @Api(description = "Операции по взаимодействию с задачами")
        @RestController
        @RequestMapping("task")
        @ComponentScan(value = "com.riatServer.service.impl")
        public class TaskController {
            @Autowired
            TaskServiceImpl taskService;

            @Autowired
            ListOfEmployeeServiceImpl listOfEmployeeService;

            @Autowired
            UsersRepo userService;


            @ApiOperation(value = "Получения списка всех задач")
            @GetMapping
            public ResponseEntity<List<Task>> List(){
                List<Task> tasks = taskService.getAll();
                if(tasks.isEmpty()){
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(tasks, HttpStatus.OK);
            }

            @ApiOperation(value = "Получения списка всех задач")
            @GetMapping("{id}")
            public ResponseEntity<Task> List(@PathVariable("id") Long id){
                Task task = taskService.getById(id);
                return new ResponseEntity<>(task, HttpStatus.OK);
            }

            @ApiOperation(value = "Получения задачи юзера")
            @GetMapping("user/{id}/{userId}")
            public ResponseEntity<EmployeeTaskDto> List(@PathVariable("id") Long id, @PathVariable("userId") Long userId){
                Task task = taskService.getById(id);
                ListOfEmployees user = listOfEmployeeService.taskInfo(userId, true, id);
                if(user == null){
                    user = listOfEmployeeService.taskInfo(userId, false, id);
                }
                User tempUser = userService.getOne(user.getOwnerId());
                EmployeeTaskDto taskUser = EmployeeTaskDto.fromUser(user, task, tempUser);
                System.out.println(user);
                return new ResponseEntity<>(taskUser, HttpStatus.OK);
            }


            @ApiOperation(value = "Получения списка вsсех подзадач одной задачи")
            @GetMapping("subTask/{id}")
            public ResponseEntity<List<Task>> allSubTaskToTask(@PathVariable("id") Long taskId){
                List<Task> tasks = taskService.getAllSubTaskToTask(taskId);
                if(tasks.isEmpty()){
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(tasks, HttpStatus.OK);
            }



            @ApiOperation(value = "Создание подзадачи для задачи")
            @PostMapping("subTask/{id}/{taskId}")
            public ResponseEntity<Task> createSubTask(@PathVariable("id") Long taskId, @RequestBody Task task){
                taskService.addSubTaskToTask(task,taskId);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            @ApiOperation(value = "Получения списка всех активных задач сотрудника")
            @GetMapping("employee/{id}/active")
            public ResponseEntity<List<EmployeeTaskDto>> listOfAllActiveTaskByEmployee(@PathVariable("id") Long userId){
                List<EmployeeTaskDto> tasks = taskService.listOfAllActiveTaskByEmployee(userId);
                if(tasks.isEmpty()){
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(tasks, HttpStatus.OK);
            }

            @ApiOperation(value = "Получения списка всех неактивных задач сотрудника")
            @GetMapping("employee/{id}/inactive")
            public ResponseEntity<List<EmployeeTaskDto>> listOfAllInactiveTaskByEmployee(@PathVariable("id") Long userId){
                List<EmployeeTaskDto> taskEmployee = taskService.listOfAllInactiveTaskByEmployee(userId);
                if(taskEmployee.isEmpty()){
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(taskEmployee, HttpStatus.OK);
            }

            @ApiOperation(value = "Сохранение шаблонной задачи")
            @PostMapping("template/{id}")
            public ResponseEntity<Task> allTemplateTask(@PathVariable("id") Long taskId){
                taskService.setTemplateTask(taskId);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            @ApiOperation(value = "Сохранение периодичной задачи")
            @PostMapping("period/{markId}/{taskId}")
            public ResponseEntity<Task> setPeriodicTask(@PathVariable("markId") Long markId, @PathVariable("taskId") Long taskId){
                taskService.setPeriodicTask(markId, taskId);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            @ApiOperation(value = "Изменение статуса задачи")
            @PostMapping("status/{taskId}/{userId}/{taskStatusId}")
            public ResponseEntity<Task> setTaskStatus(
                    @PathVariable("taskId") Long taskId,
                    @PathVariable("userId") Long userId,
                    @PathVariable("taskStatusId") Long taskStatusId)
            {
                taskService.changeTaskProgress(taskId, taskStatusId, userId);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            @ApiOperation(value = "Создание задачи")
            @PostMapping
            public ResponseEntity<Task> create(@RequestBody TaskSaveDto task){
                System.out.println(task);
                if(task == null){
                    return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                Task tempTask = taskService.createTask(task);
                return  new ResponseEntity<>(tempTask, HttpStatus.CREATED);
            }

            @ApiOperation(value = "Обновление задачи")
            @PutMapping("{id}")
            public ResponseEntity<Task> update(
                    @PathVariable("id") Long taskId,
                    @RequestBody Task task
            )
            {
                Task taskFromDb = taskService.getById(taskId);
                if(taskFromDb == null){
                    return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                BeanUtils.copyProperties(task, taskFromDb, "id");
                taskService.save(taskFromDb);
                return new ResponseEntity<>(taskFromDb, HttpStatus.OK);
            }

            @ApiOperation(value = "Удаление задачи")
            @DeleteMapping("{id}")
            public  ResponseEntity<Task> delete(@PathVariable("id") Long taskId) throws IOException, ServiceException {
                taskService.delete(taskId);
                return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        }