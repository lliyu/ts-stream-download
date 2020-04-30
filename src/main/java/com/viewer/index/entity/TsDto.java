package com.viewer.index.entity;

import java.io.Serializable;

public class TsDto implements Serializable {

    private String url;
    private String name;
    private double begin;

    private String path;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBegin() {
        return begin;
    }

    public void setBegin(double begin) {
        this.begin = begin;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
