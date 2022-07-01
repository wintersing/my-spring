package com.winter.test.aop;

public class MessageInfo {
    private int messageID;
    private String messageInfo;

    public MessageInfo(int messageID, String messageInfo) {
        this.messageID = messageID;
        this.messageInfo = messageInfo;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }
}
