package com.angke.common.config;

import java.util.Properties;

import com.angke.common.utils.PropertiesLoader;


public class GlobalConfig {

	private static final String DEFAULT_FILE_NAME = "/GlobalConfig.properties";
	private GlobalConfig(){}
	private static Properties p;
	static{
		p = PropertiesLoader.load(DEFAULT_FILE_NAME);
	}
	public static String get(String key){
		return null==p?"":p.getProperty(key);
	}
}
