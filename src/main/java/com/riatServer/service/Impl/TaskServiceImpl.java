package com.riatServer.service.Impl;

import com.riatServer.domain.*;
import com.riatServer.domain.Task;
import com.riatServer.dto.EmployeeTaskDto;
import com.riatServer.dto.TaskSaveDto;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.*;
import com.riatServer.service.EntityService;
import com.riatServer.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService, EntityService<Task, Long> {
    @Autowired
    TasksRepo taskRepo;
    @Autowired
    ListOfTasksRepo listOfTasksRepo;
    @Autowired
    ListOfEmployeesRepo listOfEmployeesRepo;
    @Autowired
    PeriodicTasksRepo periodicTasksRepo;
    @Autowired
    MarksRepo marksRepo;
    @Autowired
    TaskStatusRepo taskStatusRepo;
    @Autowired
    UsersRepo usersRepo;

    @Override
    public List<Task> getAll() {
        return  taskRepo.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
    }

    @Override
    public Task getById(Long id) {
        return taskRepo.findById(id).orElse(null);
    }

    @Override
    public Task save(Task task) {
        task.setUpdateDate(LocalDateTime.now());
        return taskRepo.save(task);
    }

    @Override
    public Task create(Task task) {
        task.setCreateDate(LocalDateTime.now());
        task.setUpdateDate(LocalDateTime.now());
        return taskRepo.save(task);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        Task task = getById(id);
        taskRepo.deleteById(id);
    }

    @Override
    public void assignTaskToEmployee(User user, Long taskId) {

    }

    @Override
    public void assignTaskToDepartment(Department department, Long taskId) {

    }

    @Override
    public void changeTaskProgress(Long taskId, Long taskStatusId, Long userId) {

        ListOfEmployees listOfEmployees = listOfEmployeesRepo.changeTaskStatus(taskId, userId);
        TaskStatus taskStatus = taskStatusRepo.findById(taskStatusId).orElse(null);
        listOfEmployees.setUpdateDate(LocalDateTime.now());
        listOfEmployees.setTaskStatusId(taskStatusId);
        listOfEmployees.setTaskStatus_id(taskStatus);
        if(taskStatusId==2||taskStatusId==4){
            listOfEmployees.setActive(false);
        }
        else {
            listOfEmployees.setActive(true);
        }
        listOfEmployeesRepo.save(listOfEmployees);
    }


    @Override
    public List<EmployeeTaskDto> listOfAllActiveTaskByEmployee(Long userId)
    {
        return listOfTaskToEmployee(userId, true);
    }

    @Override
    public List<Task> listOfAllActiveTaskByDepartment(Long userId) {
        return null;
    }

    @Override
    public List<EmployeeTaskDto> listOfAllInactiveTaskByEmployee(Long userId) {

        return listOfTaskToEmployee(userId, false);
    }

    @Override
    public List<Task> listOfAllInactiveTaskByDepartment(Long userId) {
        return null;
    }

    @Override
    public List<Task> getAllSubTaskToTask(Long taskId) {
        List<Long> subtasksId =  listOfTasksRepo.allSubTaskToTask(taskId);
        List<Task> tasks = new ArrayList<Task>();
        for(int i =0;i<subtasksId.size();i++){
            tasks.add(taskRepo.findById(subtasksId.get(i)).orElse(null));
        }
        return tasks;
    }

    public Task createTask(TaskSaveDto task) {
        Task tempTask = new Task();
        tempTask.setName(task.getName());
        tempTask.setDescription(task.getDescription());
        tempTask.setTemplateTask(false);
        tempTask.setProcent(0);
        tempTask.setTermDate(task.getOwnDate());
        tempTask.setCreateDate(LocalDateTime.now());
        tempTask.setUpdateDate(LocalDateTime.now());
        Task newTask = taskRepo.save(tempTask);

        for(int i =0;i<task.getUser_id().size();i++){
            ListOfEmployees tempTaskOflist = new ListOfEmployees();
            tempTaskOflist.setUpdateDate(LocalDateTime.now());
            tempTaskOflist.setCreateDate(LocalDateTime.now());
            tempTaskOflist.setOwnerId(task.getOwner_id());
            tempTaskOflist.setActive(true);
            tempTaskOflist.setUserId(task.getUser_id().get(i));
            tempTaskOflist.setTaskStatusId(2);
            tempTaskOflist.setTaskId(newTask.getId());
            tempTaskOflist.setTaskStatus_id(taskStatusRepo.getOne(1l));
            tempTaskOflist.setUser_id(usersRepo.getOne(task.getUser_id().get(i)));
            tempTaskOflist.setOwner_id(usersRepo.getOne(task.getOwner_id()));
            tempTaskOflist.setTask_id(newTask);

            listOfEmployeesRepo.save(tempTaskOflist);
        }
        return newTask;
    }

    @Override
    public void addSubTaskToTask(Task task, Long taskId) {
        create(task);
        Task topTask = getById(taskId);
        Task subTask = getById(task.getId());
        ListOfTask listOfTask = new ListOfTask();
        listOfTask.setSubtaskId(task.getId());
        listOfTask.setTopId(taskId);
        listOfTask.setTopTask(topTask);
        listOfTask.setSubtask(subTask);
        listOfTask.setCreateDate(LocalDateTime.now());
        listOfTask.setUpdateDate(LocalDateTime.now());
        listOfTasksRepo.save(listOfTask);
    }

    @Override
    public void addCreatedSubTaskToTaskAndInstructToUserThis(Task task, ListOfEmployees value) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        addSubTaskToTask(task, value.getTaskId());

        ListOfEmployees list = new ListOfEmployees();
        list.setUser_id(value.getUser_id());
        list.setUserId(value.getUserId());
        list.setTask_id(getById(task.getId()));
        list.setTaskId(task.getId());
        list.setOwner_id(usersRepo.findByName(authentication.getName()));
        list.setOwnerId(usersRepo.findByName(authentication.getName()).getId());
        list.setActive(true);
        list.setTaskStatus_id(taskStatusRepo.findByStatus("В процессе"));
        list.setTaskStatusId(taskStatusRepo.findByStatus("В процессе").getId());
        list.setCreateDate(LocalDateTime.now());
        list.setUpdateDate(LocalDateTime.now());
        listOfEmployeesRepo.save(list);
    }



    @Override
    public List<Task> getAllTemplateTask() {
        return taskRepo.allTemplateTask(true);
    }

    @Override
    public void setTemplateTask(Long taskId) {
        Task task = getById(taskId);
        task.setTemplateTask(true);
        task.setUpdateDate(LocalDateTime.now());
        taskRepo.save(task);
    }

    @Override
    public void setPeriodicTask(Long markId, Long taskId) {
        Task task = getById(taskId);
        Mark mark = marksRepo.findById(markId).orElse(null);
        PeriodicTask periodicTask = new PeriodicTask();
        periodicTask.setCreateDate(LocalDateTime.now());
        periodicTask.setTaskId(taskId);
        periodicTask.setActive(true);
        periodicTask.setMarkId(markId);
        periodicTask.setMark(mark);
        periodicTask.setTask(task);
        periodicTasksRepo.save(periodicTask);
    }

    public List<EmployeeTaskDto> listOfTaskToEmployee(Long userId, boolean active)
    {
        List<ListOfEmployees> listOfEmployees = listOfEmployeesRepo.listOfAllActiveTaskByEmployee(userId, active);
        List<Task> tasks = new ArrayList<Task>();
        ListOfEmployees tempListOfEmpl = new ListOfEmployees();
        for(int i =0;i<listOfEmployees.size();i++){
            tempListOfEmpl = listOfEmployees.get(i);
            tasks.add(taskRepo.findById(tempListOfEmpl.getTaskId()).orElse(null));
        }
        List <EmployeeTaskDto> employeeTaskDto = new ArrayList<>();
        User user = new User();
        for(int i=0;i<listOfEmployees.size();i++){
            //System.out.println(listOfTasksRepo.countTask(listOfEmployees.get(i).getTaskId()));
            //System.out.println();
            //if(listOfTasksRepo.countTask(listOfEmployees.get(i).getTaskId()) < 1)
            user= usersRepo.getOne(listOfEmployees.get(i).getOwnerId());
            employeeTaskDto.add(EmployeeTaskDto.fromUser(listOfEmployees.get(i),tasks.get(i), user));
        }
        return employeeTaskDto;}

    @Override
    public List<Task> getAll(String value) {
        if(value == null || value.isEmpty()){
            return taskRepo.findAll();
        } else {
            return taskRepo.search(value);
        }
    }

    @Override
    public void delete(Task task) {
        taskRepo.delete(task);
    }

    @Override
    public List<Task> getRootTasks(String s) {
        return taskRepo.allRootTasks(s);
    }

    @Override
    public List<Task> getChildTasks(Task task, String s) {
        return taskRepo.getAllChildTasks(task.getId(), s);
    }

    @Override
    public List<Task> getTasksByStatus(long statusId, LocalDateTime fromCreateDate, LocalDateTime toCreateDate,
                                       LocalDateTime fromTermDate, LocalDateTime toTermDate) {
        return taskRepo.getTaskByStatus(statusId, fromCreateDate, toCreateDate, fromTermDate, toTermDate);
    }


}
