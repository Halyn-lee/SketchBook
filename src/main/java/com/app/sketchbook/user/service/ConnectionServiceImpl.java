package com.app.sketchbook.user.service;

import com.app.sketchbook.user.DTO.ConnectionLogDTO;
import com.app.sketchbook.user.entity.ConnectionLog;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.ConnectionLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.http.HttpRequest;
import java.util.Date;
import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionLogService{

    private final ConnectionLogRepository connectionLogRepository;

    @Override
    public void insertConnection(HttpServletRequest request, SketchUser user) {
        var connectionLog = new ConnectionLog();

        connectionLog.setUser(user);
        connectionLog.setConnectedTime(new Date());
        connectionLog.setBrowser(getBrowser(request.getHeader("User-Agent")));
        String ip = getIp(request);
        connectionLog.setIp(ip);

        RestTemplate rest = new RestTemplate();
        String result = rest.getForObject("http://ip-api.com/csv/"+ip+"?fields=country", String.class);

        connectionLog.setRegion(result);

        connectionLogRepository.save(connectionLog);
    }

    @Override
    public List<ConnectionLogDTO> findAllLogsByUser(SketchUser user) {
        return connectionLogRepository.findAllByUser(user);
    }

    private String getBrowser(String agent){
        // 브라우져 구분
        String browser = null;
        if (agent != null) {
            if (agent.contains("Trident")) {
                browser = "MSIE";
            } else if (agent.contains("Chrome")) {
                browser = "Chrome";
            } else if (agent.contains("Opera")) {
                browser = "Opera";
            } else if (agent.contains("iPhone") && agent.contains("Mobile")) {
                browser = "iPhone";
            } else if (agent.contains("Android") && agent.contains("Mobile")) {
                browser = "Android";
            }
        }
        log.info("Result: Browser: "+browser);

        return browser;
    }

    private String getIp(HttpServletRequest request){
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        String ip = null;

        for(String header : headers) {
            if(ip==null){
                ip = request.getHeader(header);
            }
        }

        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        // 로컬일 경우 공인 IP 획득
        if(ip.equals("0:0:0:0:0:0:0:1")){
            try{
                RestTemplate rest = new RestTemplate();
                ip = rest.getForObject("https://checkip.amazonaws.com/", String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("Result : IP Address : "+ip);

        return ip;
    }
}
