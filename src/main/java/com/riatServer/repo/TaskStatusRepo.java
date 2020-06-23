package com.riatServer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.riatServer.domain.TaskStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskStatusRepo extends JpaRepository<TaskStatus, Long> {
    @Query("select t from TaskStatus t " +
            "where lower(t.Name) like lower(concat('%', :searchTerm, '%')) order by t.id asc"
    )
    List<TaskStatus> search(@Param("searchTerm") String searchTerm);

    @Query("select t from  TaskStatus t where t.Name = ?1")
    TaskStatus findByStatus(String name);
}
