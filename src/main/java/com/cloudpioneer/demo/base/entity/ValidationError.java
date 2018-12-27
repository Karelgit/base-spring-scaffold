package com.cloudpioneer.demo.base.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author jyj
 */
@Data
public class ValidationError implements Serializable {

    private static final long serialVersionUID = 1L;

    private String field;
    private Object value;
    private String cause;

    public ValidationError() {
    }

    public ValidationError(String field, Object value, String cause) {
        this.field = field;
        this.value = value;
        this.cause = cause;
    }
}
