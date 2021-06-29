package com.example.graduationproject.models;


public class SpinnerModel {
    private String text;
    private int icon;

    public SpinnerModel(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public int getIcon() {
        return icon;
    }
}