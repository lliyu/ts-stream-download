package com.viewer.index.entity;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.Serializable;

public class IndexPageEntity implements Serializable {

    private Label path;
    private Label log;
    private TextField m3u8;
    private TextField name;
    private TextField prefix;
    private TextArea info;

    public Label getPath() {
        return path;
    }

    public void setPath(Label path) {
        this.path = path;
    }

    public Label getLog() {
        return log;
    }

    public void setLog(Label log) {
        this.log = log;
    }

    public TextField getM3u8() {
        return m3u8;
    }

    public void setM3u8(TextField m3u8) {
        this.m3u8 = m3u8;
    }

    public TextField getName() {
        return name;
    }

    public void setName(TextField name) {
        this.name = name;
    }

    public TextArea getInfo() {
        return info;
    }

    public void setInfo(TextArea info) {
        this.info = info;
    }

    public TextField getPrefix() {
        return prefix;
    }

    public void setPrefix(TextField prefix) {
        this.prefix = prefix;
    }
}
