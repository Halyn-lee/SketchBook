package com.app.sketchbook.user.repository;

import com.app.sketchbook.user.entity.ConnectionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
}
