package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.ConnectionLog;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ConnectionLogService {
    void insertConnection(HttpServletRequest request, SketchUser user);
    Page<ConnectionLog> findAllLogsByUser(int page, SketchUser user);
}
