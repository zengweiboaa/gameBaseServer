package com.angke.common.exception;

/**
 * 适用于读取配置文件时产生的异常
 *
 */
public class MethodNotSupportedException extends RuntimeException {
	/** */
	private static final long serialVersionUID = 1L;

	public MethodNotSupportedException(String msg) {
		super(msg);
	}

	public MethodNotSupportedException(Exception e) {
		super(e);
	}

	public MethodNotSupportedException(String msg, Exception e) {
		super(msg, e);
	}
}
