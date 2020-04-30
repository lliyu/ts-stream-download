package com.viewer.index.entity;

import java.io.Serializable;

public class M3U8InfoDTO implements Serializable {

    private double sec;
    private double size;

    public double getSec() {
        return sec;
    }

    public void setSec(double sec) {
        this.sec = sec;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
