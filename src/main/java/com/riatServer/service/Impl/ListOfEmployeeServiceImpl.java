package com.riatServer.service.Impl;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.TaskStatus;
import com.riatServer.domain.User;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.ListOfEmployeesRepo;
import com.riatServer.repo.TaskStatusRepo;
import com.riatServer.repo.TasksRepo;
import com.riatServer.repo.UsersRepo;
import com.riatServer.service.EntityService;
import com.riatServer.service.ListOfEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListOfEmployeeServiceImpl implements ListOfEmployeeService, EntityService<ListOfEmployees, Long> {
    @Autowired
    ListOfEmployeesRepo listOfEmployeesRepo;
    @Autowired
    TasksRepo tasksRepo;
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    TaskStatusRepo taskStatusRepo;


    @Override
    public List<ListOfEmployees> getAll() {
        return listOfEmployeesRepo.findAll();
    }

    public List<ListOfEmployees> statistic(long userId) {
        return listOfEmployeesRepo.statistic(userId);
    }

    @Override
    public ListOfEmployees getById(Long id) {
        return listOfEmployeesRepo.findById(id).orElse(null);
    }

    @Override
    public ListOfEmployees save(ListOfEmployees listOfEmployees) {
        listOfEmployees.setUpdateDate(LocalDateTime.now());
        return listOfEmployeesRepo.save(listOfEmployees);
    }

    @Override
    public ListOfEmployees create(ListOfEmployees listOfEmployees) {
        Task task = tasksRepo.findById(listOfEmployees.getTaskId()).orElse(null);
        User user = usersRepo.findById(listOfEmployees.getUserId()).orElse(null);
        listOfEmployees.setUser_id(user);
        listOfEmployees.setTask_id(task);
        listOfEmployees.setCreateDate(LocalDateTime.now());
        listOfEmployees.setUpdateDate(LocalDateTime.now());
        return listOfEmployeesRepo.save(listOfEmployees);
    }

    @Override
    public ListOfEmployees create2(ListOfEmployees listOfEmployees) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TaskStatus taskStatus = taskStatusRepo.findByStatus("В процессе");
        listOfEmployees.setOwner_id(usersRepo.findByName(authentication.getName()));
        listOfEmployees.setOwnerId(usersRepo.findByName(authentication.getName()).getId());
        listOfEmployees.setActive(true);
        listOfEmployees.setTaskStatus_id(taskStatus);
        listOfEmployees.setTaskStatusId(taskStatus.getId());
        listOfEmployees.setCreateDate(LocalDateTime.now());
        listOfEmployees.setUpdateDate(LocalDateTime.now());
        return listOfEmployeesRepo.save(listOfEmployees);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        ListOfEmployees listOfEmployees = getById(id);
        listOfEmployeesRepo.delete(listOfEmployees);
    }

    @Override
    public List<ListOfEmployees> getAll(String value) {
        if(value == null || value.isEmpty()){
            return listOfEmployeesRepo.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
        } else {
            return listOfEmployeesRepo.search(value);
        }
    }

    @Override
    public ListOfEmployees taskInfo(Long userId, boolean active, Long taskId) {
        return listOfEmployeesRepo.activeTask(userId, active, taskId);
    }

    @Override
    public void delete(ListOfEmployees listOfEmployees) {
        listOfEmployeesRepo.delete(listOfEmployees);
    }

    @Override
    public List<ListOfEmployees> getRootTasks(String s) {
        return listOfEmployeesRepo.allRootTasks(s);
    }

    @Override
    public List<ListOfEmployees> getChildTasks(ListOfEmployees listOfEmployees, String s) {
        return listOfEmployeesRepo.getAllChildTasks(listOfEmployees.getTaskId(), s);
    }

    @Override
    public List<ListOfEmployees> checkUniqueInstruct(long id, long id1) {
        return listOfEmployeesRepo.checkIsInstructData(id, id1);
    }

    @Override
    public List<ListOfEmployees> getRootTasksForCurrentUser(String s) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return listOfEmployeesRepo.allRootTasksForCurrentUser(s, authentication.getName());
    }

    @Override
    public List<ListOfEmployees> getChildTasksForCurrentUser(ListOfEmployees listOfEmployees, String s) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return listOfEmployeesRepo.getAllChildTasksForCurrentUser(listOfEmployees.getTaskId(), s, authentication.getName());
    }

    @Override
    public List<ListOfEmployees> getRootTasksForCurrentUserByTaskStatus(TaskStatus taskStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return listOfEmployeesRepo.allRootTasksForCurrentUserByTaskStatus(taskStatus.getId(), authentication.getName());
    }

    @Override
    public List<ListOfEmployees> getChildTasksForCurrentUserByTaskStatus(ListOfEmployees listOfEmployees, TaskStatus taskStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return listOfEmployeesRepo.getAllChildTasksForCurrentUserByTaskStatus(listOfEmployees.getTaskId(), taskStatus.getId(), authentication.getName());
    }

    @Override
    public List<ListOfEmployees> getTheUserTasksByStatus(long id, LocalDateTime value, LocalDateTime value1, LocalDateTime value2, LocalDateTime value3) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return listOfEmployeesRepo.getTheUserTasksByStatus(id, value, value1, value2, value3, authentication.getName());
    }
}
