package com.cloudpioneer.demo.base.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果类
 * @author jiangyunjun
 */
@Data
public class Result implements Serializable{

    private int code;

    private String msg;

    private Object data;

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(FailureResult failureResult, Object data) {
        this.code = failureResult.getCode();
        this.msg = failureResult.getMessage();
        this.data = data;
    }

}
