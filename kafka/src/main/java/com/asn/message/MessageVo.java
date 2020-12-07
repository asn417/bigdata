package com.asn.message;

import java.time.LocalDateTime;

public class MessageVo {
    private int userID;
    private String clientIP;
    private LocalDateTime logTime;

    public MessageVo(int userID, String clientIP, LocalDateTime logTime) {
        this.userID = userID;
        this.clientIP = clientIP;
        this.logTime = logTime;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }
}
