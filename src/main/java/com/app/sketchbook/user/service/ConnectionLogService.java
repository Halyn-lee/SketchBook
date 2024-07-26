package com.app.sketchbook.user.service;

import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;

public interface ConnectionLogService {
    void insertConnection(HttpServletRequest request, boolean success);
}
