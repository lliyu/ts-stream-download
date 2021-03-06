package com.viewer.index.entity;

import java.io.Serializable;

public class TsEntity implements Serializable {
    private int count;
    private String ts;
    private String path;
    private int retry;//当前重试次数
    private String m3u8Prefix;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public String getM3u8Prefix() {
        return m3u8Prefix;
    }

    public void setM3u8Prefix(String m3u8Prefix) {
        this.m3u8Prefix = m3u8Prefix;
    }

    @Override
    public String toString() {
        return "TsEntity{" +
                "count=" + count +
                ", ts='" + ts + '\'' +
                ", path='" + path + '\'' +
                ", retry=" + retry +
                '}';
    }
}
