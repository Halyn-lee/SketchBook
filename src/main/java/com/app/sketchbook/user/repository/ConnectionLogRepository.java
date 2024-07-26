package com.app.sketchbook.user.repository;

import com.app.sketchbook.user.DTO.ConnectionLogDTO;
import com.app.sketchbook.user.entity.ConnectionLog;
import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
    List<ConnectionLogDTO> findAllByUser(SketchUser user);
}
