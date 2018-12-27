package com.cloudpioneer.demo.base.entity;

import lombok.Getter;

/**
 * 全局失败结果枚举类
 * @author jiangyunjun
 */
@Getter
public enum FailureResult {

    /**
     * 具体失败项code以及失败描述
     */
    FAILURE(400,"操作失败！");


	private int code;
	private String message;

	FailureResult(int code, String message){
		this.code = code;
		this.message = message;
	}

	@Override
	public String toString(){
		return this.name()+"-"+this.getCode()+"-"+this.getMessage();
	}

}
