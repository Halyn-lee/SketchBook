package com.app.sketchbook.user.service;

import com.app.sketchbook.user.DTO.ConnectionLogDTO;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;
import java.util.List;

public interface ConnectionLogService {
    void insertConnection(HttpServletRequest request, SketchUser user);
    List<ConnectionLogDTO> findAllLogsByUser(SketchUser user);
}
