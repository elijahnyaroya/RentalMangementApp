package com.elisuntech.rentalmanagementapp2.DMC;

public class messagechatDMC {
    String tenantID;
    String messageID;
    String message;
    String sender;

    public messagechatDMC(String tenantID, String messageID, String message, String sender) {
        this.tenantID = tenantID;
        this.messageID = messageID;
        this.message = message;
        this.sender = sender;
    }

    public String getTenantID() {
        return tenantID;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
