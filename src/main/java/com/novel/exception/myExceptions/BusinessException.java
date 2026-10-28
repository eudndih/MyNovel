package com.novel.exception.myExceptions;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    public BusinessException( String message) {
        super(message);
        this.code = 400;
    }
}
