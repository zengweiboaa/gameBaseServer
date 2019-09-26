package com.angke.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angke.common.annotation.MsgToExec;
import com.angke.common.utils.ClassLoadUtils;
import com.angke.common.utils.PropertiesLoader;
import com.angke.common.utils.TypeConvUtil;

public class MsgConfig<M> {
	
	private static final Logger LOG = LoggerFactory.getLogger(MsgConfig.class);

	private MsgConfig() {
	}

	// 消息协议码Short -> 消息的对象类型Class
	private static Map<Short, Class<Object>> msgCodeToMsgClassMap;

	// 消息的对象类型Class -> 消息协议码Short
	private static Map<Class<Object>, Short> msgClassToMsgCodeMap;

	// 消息协议码Short -> 业务逻辑执行器的对象类型Class
	private static Map<Short, Class<?>> msgCodeToExecutorClassMap;
	
	/**
	 * MsgHelper 初始化，msgClass，msgCode，msgExecutor映射关系建立
	 * @param msgToCodePropFilePath 消息对象类型和消息码的映射配置文件，如："./proto/MsgToCode.properties"
	 * @param msgToExecPackageName 业务执行器的包名，如："com.angke.tianfu.server.executors"
	 * @param isRecursive 在扫描包下的注解时，是否需要迭代遍历
	 */
	public static void initMsgHelper(String msgToCodePropFilePath, String msgToExecPackageName, boolean isRecursive) {
		LOG.info("MsgHelper starts initialization");
		LOG.info("msgCodeToMsgClassMap And msgClassToMsgCodeMap are initializing");
		initMsgCodeToMsgClassMapAndMsgClassToMsgCodeMap(msgToCodePropFilePath);
		LOG.info("msgCodeToExecutorClassMap is initializing");
		initMsgCodeToExecutorClassMap(msgToExecPackageName,isRecursive);
		LOG.info("{}", msgCodeToMsgClassMap);
		LOG.info("{}", msgClassToMsgCodeMap);
		LOG.info("{}", msgCodeToExecutorClassMap);
		LOG.info("MsgHelper initialization completed");
	}

	@SuppressWarnings({ "unchecked" })
	private static void initMsgCodeToMsgClassMapAndMsgClassToMsgCodeMap(String msgToCodePropFilePath) {

		Map<Class<Object>, Short> tmpMsgClassToMsgCodeMap = new HashMap<Class<Object>, Short>();
		Map<Short, Class<Object>> tmpMsgCodeToMsgClassMap = new HashMap<Short, Class<Object>>();

		PropertiesLoader propertiesLoader = new PropertiesLoader(msgToCodePropFilePath);
		Map<?, ?> msgClassAndCode = propertiesLoader.getAll();
		Iterator<?> msgClassAndCodeIterator = msgClassAndCode.entrySet().iterator();
		while (msgClassAndCodeIterator.hasNext()) {
			Map.Entry<String, String> classAndCodeEntry = (Map.Entry<String, String>) msgClassAndCodeIterator.next();
			try {
				// 通过反射从类名拿到消息的对象类型
				Class<Object> msgClass = (Class<Object>) Class.forName(classAndCodeEntry.getKey());
				tmpMsgClassToMsgCodeMap.put(msgClass, TypeConvUtil.StrHexToShort(classAndCodeEntry.getValue()));
				tmpMsgCodeToMsgClassMap.put(TypeConvUtil.StrHexToShort(classAndCodeEntry.getValue()), msgClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		msgClassToMsgCodeMap = Collections.unmodifiableMap(tmpMsgClassToMsgCodeMap);
		msgCodeToMsgClassMap = Collections.unmodifiableMap(tmpMsgCodeToMsgClassMap);
	}

	private static void initMsgCodeToExecutorClassMap(String msgToExecPackageName, boolean isRecursive) {

		Map<Short, Class<?>> tmpMap = new HashMap<Short, Class<?>>();

		List<Class<?>> classList = ClassLoadUtils.getClassList(msgToExecPackageName,isRecursive);
		if (classList != null) {
			for (Class<?> clazz : classList) {
				if (clazz.isAnnotationPresent(MsgToExec.class)) {
					MsgToExec annotation = clazz.getAnnotation(MsgToExec.class);
					tmpMap.put(annotation.msgType(), clazz);
				}
			}
		}

		msgCodeToExecutorClassMap = Collections.unmodifiableMap(tmpMap);
	}

	/**
     * 根据消息协议码取得消息的对象类型
     * @param msgCode
     * @return
     */
	public static Class<Object> getMsgClassByMsgCode(short msgCode) {
		return msgCodeToMsgClassMap.get(msgCode);
	}
 	
	/**
	 * 根据消息的对象类型取得消息协议码
	 * @param msgClass
	 * @return
	 */
	public static short getMsgCodeByMsgClass(Class<? extends Object> msgClass) {
		return msgClassToMsgCodeMap.get(msgClass);
	}
	
	/**
	 * 根据消息协议码取得对象的业务执行对象类型
	 * @param msgCode
	 * @return
	 */
	public static Class<?> getExecutorClassByMsgCode(short msgCode) {
		return msgCodeToExecutorClassMap.get(msgCode);
	}
	
	/**
	 * 拿到msgCode -> msgClass 的映射
	 * @return
	 */
	public static Map<Short, Class<Object>> getMsgCodeToMsgClassMap() {
		return msgCodeToMsgClassMap;
	}

	/**
	 * 拿到msgClass -> msgCode 的映射
	 * @return
	 */
	public static Map<Class<Object>, Short> getMsgClassToMsgCodeMap() {
		return msgClassToMsgCodeMap;
	}

	/**
	 * 拿到msgCode -> executorClass 的映射
	 * @return
	 */
	public static Map<Short, Class<?>> getMsgCodeToExecutorClassMap() {
		return msgCodeToExecutorClassMap;
	}
}
