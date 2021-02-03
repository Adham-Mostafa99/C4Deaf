package com.example.graduationproject;

public class StringMsg {
    private StringBuilder msg;

    public StringMsg() {
        this.msg = new StringBuilder();
        this.msg.append("{");
    }

    public void setIntElementOfMsg(String elementName, int element) {
        this.msg.append("\"").append(elementName).append("\":");
        this.msg.append(element).append(",");
    }

    public void setStringElementOfMsg(String elementName, String element) {
        this.msg.append("\"").append(elementName).append("\":");
        this.msg.append("\"").append(element).append("\",");
    }

    public void setLastIntElementOfMsg(String elementName, int element) {
        this.msg.append("\"").append(elementName).append("\":");
        this.msg.append(element).append("}");
    }

    public void setLastStringElementOfMsg(String elementName, String element) {
        this.msg.append("\"").append(elementName).append("\":");
        this.msg.append("\"").append(element).append("\"}");
    }

    public String getMsg() {
        return msg.toString();
    }
}
