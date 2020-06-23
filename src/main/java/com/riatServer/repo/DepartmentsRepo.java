package com.riatServer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.riatServer.domain.Department;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentsRepo extends JpaRepository<Department, Long> {
    @Query("select d from Department d " +
            "where lower(d.Name) like lower(concat('%', ?1, '%')) order by d.createDate asc"
    )
    List<Department> search(String value);
}
