package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.SketchUser;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;

public interface ConnectionLogService {
    void insertConnection(HttpServletRequest request, SketchUser user);
}
