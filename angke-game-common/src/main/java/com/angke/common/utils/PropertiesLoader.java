package com.angke.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

	private Properties props;

	public static Properties load(String fileName) {
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = PropertiesLoader.class.getResourceAsStream(fileName);
			br = new BufferedReader(new InputStreamReader(in));
			Properties p = new Properties();
			p.load(br);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
    public PropertiesLoader(String fileName) {
    	readProperties(fileName);
    }
    
    /**
     * 加载配置文件
     * 
     * @param fileName
     */
    @SuppressWarnings("unused")
	private void readProperties(String fileName) {
    	props = PropertiesLoader.load(fileName);
    }

    /**
     * 根据key读取对应的value
     * @param key
     * @return
     */
    public String get(String key) {
        return props.getProperty(key);
    }

    /**
     * 根据key和value的class类型获取对应的value
     * @param key
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> T get(String key,Class<T> valueClass){
    	return (T)get(key);
    }
    
    /**
     * 得到所有的配置信息
     * 
     * @return
     */
    public Map<?, ?> getAll() {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<?> enu = props.propertyNames();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = props.getProperty(key);
            map.put(key, value);
        }
        return map;
    }
}
