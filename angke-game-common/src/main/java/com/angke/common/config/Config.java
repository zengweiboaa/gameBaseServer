package com.angke.common.config;

public interface Config {

	void loadProp();

	String get(String key);
	
	boolean filePathExist();
}
