package com.riatServer.repo;

import com.riatServer.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionsRepo extends JpaRepository<Position, Long> {
    @Query("select p from Position p " +
            "where lower(p.Name) like lower(concat('%', :searchTerm, '%')) order by p.id asc"
            //        + "or lower(u.telephone) like lower(concat('%', :searchTerm, '%'))"
    )
    List<Position> search(@Param("searchTerm") String searchTerm);
}
