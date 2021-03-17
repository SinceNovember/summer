package com.summer.exception;

/**
 * 接口没有实现类异常
 **/
public class InterfaceNotHaveImplementedClassException extends RuntimeException {

    public InterfaceNotHaveImplementedClassException(String message) {
        super(message);
    }
}
