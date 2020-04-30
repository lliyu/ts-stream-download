package com.viewer.index.response;

import java.io.Serializable;

/**
 * 请求响应
 */
public class BaseResponse implements Serializable {

    private int status;

    private String msg;

    private Object data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public BaseResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public BaseResponse(int status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
