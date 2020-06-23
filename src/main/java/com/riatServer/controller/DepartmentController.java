
package com.riatServer.controller;

import com.riatServer.domain.Department;
import com.riatServer.dto.DepartmentListDto;
import com.riatServer.repo.DepartmentsRepo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(description = "Операции по взаимодействию с отделами")
@RestController
@RequestMapping("department")
public class DepartmentController {
    private final DepartmentsRepo departmentRepo;

    @Autowired
    public DepartmentController(DepartmentsRepo departmentsRepo)
    {
        this.departmentRepo = departmentsRepo;
    }

    @ApiOperation(value = "Получения списка всех отделов")
    @GetMapping
    public ResponseEntity<List<DepartmentListDto>> List(){
        List<Department> departments = departmentRepo.findAll();
        List<DepartmentListDto> dep = new ArrayList<>();
        for(int i=0;i<departments.size();i++){
            dep.add(DepartmentListDto.fromJson(departments.get(i)));
        }
        if(departments.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dep, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание отдела")
    @PostMapping
    public ResponseEntity<Department> create(@RequestBody Department  department){
        if(department == null){
            return   new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        departmentRepo.save(department);
        return  new ResponseEntity<>(department, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление отдела")
    @PutMapping("{id}")
    public ResponseEntity<Department> update(
            @PathVariable("id") Long departmentId,
            @RequestBody Department department
    )
    {
        Department departmentFromDb = departmentRepo.findById(departmentId).orElse(null);
        if(departmentFromDb == null){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(department, departmentFromDb, "id");
        departmentRepo.save(departmentFromDb);
        return new ResponseEntity<>(departmentFromDb, HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление отдела")
    @DeleteMapping("{id}")
    public  ResponseEntity<Department> delete(@PathVariable("id") Long departmentId){
        Department department = departmentRepo.findById(departmentId).orElse(null);
        if(department == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        departmentRepo.delete(department);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}