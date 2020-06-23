package com.riatServer.repo;

import com.riatServer.domain.Mark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarksRepo extends JpaRepository<Mark, Long> {
}
