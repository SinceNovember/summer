package com.summer.exception;

/**
 * 不能初始化构造函数
 */
public class CannotInitializeConstructorException extends RuntimeException {

    public CannotInitializeConstructorException(String message) {
        super(message);
    }
}
