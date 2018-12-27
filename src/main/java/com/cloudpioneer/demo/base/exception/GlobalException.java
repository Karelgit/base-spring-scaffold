package com.cloudpioneer.demo.base.exception;

import com.cloudpioneer.demo.base.entity.FailureResult;

import javax.validation.constraints.NotNull;

/**
 * 全局异常类，继承RuntimeException
 * RuntimeException可以直接断开事务
 * @author jiangyunjun
 */
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    /**
     * 对外提供的构造方法
     * @param failureResult 失败结果枚举
     */
    public GlobalException(@NotNull FailureResult failureResult) {
        super(failureResult.getMessage());
        this.code = failureResult.getCode();
    }

    public GlobalException(@NotNull FailureResult failureResult,@NotNull String msg) {
        super(failureResult.getMessage(),new Throwable(msg));
        this.code = failureResult.getCode();
    }

    int getCode() {
        return code;
    }
}
