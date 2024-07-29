package com.app.sketchbook.user.repository;

import com.app.sketchbook.user.entity.ConnectionLog;
import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
    Page<ConnectionLog> findAllByUserOrderByConnectedTimeDesc(Pageable pageable, SketchUser user);
}
