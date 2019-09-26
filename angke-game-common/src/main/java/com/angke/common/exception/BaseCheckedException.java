package com.angke.common.exception;

/**
 * 适用于读取配置文件时产生的异常
 *
 */
public class BaseCheckedException extends RuntimeException {
	/** */
	private static final long serialVersionUID = 1L;

	public BaseCheckedException(String msg) {
		super(msg);
	}

	public BaseCheckedException(Exception e) {
		super(e);
	}

	public BaseCheckedException(String msg, Exception e) {
		super(msg, e);
	}
}
