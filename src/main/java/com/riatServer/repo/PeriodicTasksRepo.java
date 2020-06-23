package com.riatServer.repo;

import com.riatServer.domain.PeriodicTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodicTasksRepo extends JpaRepository<PeriodicTask, Long> {
}
