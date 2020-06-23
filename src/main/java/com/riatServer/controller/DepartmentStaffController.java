
package com.riatServer.controller;

import com.riatServer.domain.DepartmentStaff;
import com.riatServer.dto.DepartmentStaffDto;
import com.riatServer.dto.EmployeeListDto;
import com.riatServer.repo.MessagesRepo;
import com.riatServer.service.Impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.riatServer.repo.DepartmentStaffsRepo;

import java.util.ArrayList;
import java.util.List;

@Api(description = "Операции по взаимодействию с составом отдела")
@RestController
@RequestMapping("departmentStaff")
public class DepartmentStaffController {
    private final DepartmentStaffsRepo departmentStaffRepo;
    private final UserServiceImpl userService;
    private final MessagesRepo messagesRepo;

    @Autowired
    public DepartmentStaffController(DepartmentStaffsRepo departmentStaffsRepo, UserServiceImpl userService, MessagesRepo messagesRepo)
    {
        this.departmentStaffRepo = departmentStaffsRepo;
        this.userService = userService;
        this.messagesRepo = messagesRepo;
    }

    @ApiOperation(value = "Получения списка всех составов отделов")
    @GetMapping
    public ResponseEntity<List<EmployeeListDto>> List(){
        List<DepartmentStaff> departmentStaffs = departmentStaffRepo.findAll();
        List<EmployeeListDto> employeeListDtos = new ArrayList<>();
        for(int i =0;i<departmentStaffs.size();i++){
            employeeListDtos.add(EmployeeListDto.fromJson(userService.getById(departmentStaffs.get(i).getUserId()), departmentStaffs.get(i)));
        }
        if(departmentStaffs.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employeeListDtos, HttpStatus.OK);
    }


    @ApiOperation(value = "Получения информации об 1 отделе")
    @GetMapping("{userId}")
    public ResponseEntity<List<DepartmentStaffDto>> List(@PathVariable("userId") Long userId){
        long departmentId = departmentStaffRepo.userDepartmentId(userId);
        List<DepartmentStaff> departmentStaffs = departmentStaffRepo.userDepartmentList(departmentId, userId);
        List<DepartmentStaffDto> departmentStaffDtos = new ArrayList<>();
        for(int i =0;i<departmentStaffs.size();i++){
            departmentStaffDtos.add(DepartmentStaffDto.fromDepartmentStaff(userService.getById(departmentStaffs.get(i).getUserId()),messagesRepo.userMsg(departmentStaffs.get(i).getUserId())));
        }
        if(departmentStaffs.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(departmentStaffDtos, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание состава отдела")
    @PostMapping
    public ResponseEntity<DepartmentStaff> create(@RequestBody DepartmentStaff  departmentStaff){
        if(departmentStaff == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        departmentStaffRepo.save(departmentStaff);
        return  new ResponseEntity<>(departmentStaff, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление состава отдела")
    @PutMapping("{id}")
    public ResponseEntity<DepartmentStaff> update(
            @PathVariable("id") Long departmentStaffId,
            @RequestBody DepartmentStaff departmentStaff
    )
    {
        DepartmentStaff departmentStaffFromDb = departmentStaffRepo.findById(departmentStaffId).orElse(null);
        if(departmentStaffFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(departmentStaff, departmentStaffFromDb, "id");
        departmentStaffRepo.save(departmentStaffFromDb);
        return new ResponseEntity<>(departmentStaffFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление состава отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<DepartmentStaff> delete(@PathVariable("id") Long departmentStaffId){
        DepartmentStaff departmentStaff = departmentStaffRepo.findById(departmentStaffId).orElse(null);
        if(departmentStaff == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        departmentStaffRepo.delete(departmentStaff);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}