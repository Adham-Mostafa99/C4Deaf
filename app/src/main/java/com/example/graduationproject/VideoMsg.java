package com.example.graduationproject;

import com.example.graduationproject.models.DeafChat;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoMsg {
    private String stringMsg;

    public VideoMsg() {

    }

    public VideoMsg(String stringMsg) {
        this.stringMsg = stringMsg;
    }

    public String setVideoData(String sender, String receiver, String path, String videoDuration, String msgTime) {

        StringMsg stringMsg = new StringMsg();
        stringMsg.setStringElementOfMsg("sender", sender);
        stringMsg.setStringElementOfMsg("receiver", receiver);
        stringMsg.setStringElementOfMsg("path", path);
        stringMsg.setStringElementOfMsg("videoDuration", videoDuration);
        stringMsg.setLastStringElementOfMsg("msgTime", msgTime);

        return stringMsg.getMsg();
    }

    public DeafChat getDeafChatMsg() {
        try {
            if (stringMsg != null) {
                JSONObject jsonObject = new JSONObject(stringMsg);

                return new DeafChat(
                        jsonObject.getString("sender"),
                        jsonObject.getString("path"),
                        jsonObject.getString("videoDuration"),
                        jsonObject.getString("msgTime")
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
