package com.riatServer.repo;

import com.riatServer.domain.DepartmentStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentStaffsRepo extends JpaRepository<DepartmentStaff, Long> {
    @Query("select u from DepartmentStaff u where u.departmentId = ?1 and u.userId <> ?2")
    List<DepartmentStaff> userDepartmentList(Long departmentId, Long userId);

    @Query("select u.departmentId from DepartmentStaff u where  u.userId = ?1")
    long userDepartmentId(Long userId);

    @Query("select ds from DepartmentStaff as ds JOIN Department as d ON ds.departmentId = d.id " +
            "where lower(d.Name) like lower(concat('%', ?1, '%')) order by ds.createDate asc"
    )
    List<DepartmentStaff> search(String value);

    @Query("select d from DepartmentStaff d where  d.userId = ?1")
    DepartmentStaff findByUserId(Long id);

    @Query("select u.userId from DepartmentStaff u where u.departmentId = ?1")
    List<Long> userAllDep(Long departmentId);

    @Query("select u.departmentId from DepartmentStaff u where u.userId = ?1")
    Long userDep(Long departmentId);
}
