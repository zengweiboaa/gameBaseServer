package com.angke.common.config;

import java.util.Properties;

import com.angke.common.exception.ConfigException;
import com.angke.common.utils.PropertiesLoader;

public abstract class AbsConfig implements Config {

	protected Properties prop;
	protected String configFilePath;
	
	public AbsConfig(String filePath) {
		if (filePath == null || filePath == "") {
			//不依赖配置文件的配置对象
		}else {

			this.configFilePath = filePath;
			if (!filePathExist()) {
				throw new ConfigException("配置文件不存在,请检查并保证书写正确的文件地址");
			}
		}
	}
	
	@Override
	public void loadProp() {

		this.prop = PropertiesLoader.load(configFilePath);
	}
	
	@Override
	public String get(String key){
		return null==prop?"":prop.getProperty(key);
	}
	
	@Override
	public boolean filePathExist() {
		loadProp();
		if (prop == null) {
			return false;
		}
		return true;
	}

}
