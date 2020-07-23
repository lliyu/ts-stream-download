package com.viewer.index.entity;

public class ConfigEntity {

    private String defaultPath;

    private int retryCount;

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return "ConfigEntity{" +
                "defaultPath='" + defaultPath + '\'' +
                ", retryCount=" + retryCount +
                '}';
    }
}
