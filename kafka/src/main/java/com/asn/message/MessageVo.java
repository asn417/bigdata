package com.asn.message;

public class MessageVo {
    private long userID;
    private String clientIP;
    private long logTime;

    public MessageVo(long userID, String clientIP, long logTime) {
        this.userID = userID;
        this.clientIP = clientIP;
        this.logTime = logTime;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }
}
