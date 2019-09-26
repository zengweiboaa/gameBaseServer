package com.angke.common.constant;

public class GlobalConfigNames {

	public static final String RUN_MODE = "RUN_MODE";
	
	/** 服务器是否暴露Http服务器 */
	public static final String HTTPSERVER_OP = "HTTPSERVER_OP";
	
	/** 服务器是否暴露Http文件服务器 */
	public static final String HTTPFILESERVER_OP = "HTTPFILESERVER_OP";
	
	/** 服务器是否暴露TCP */
	public static final String TCP_OP = "TCP_OP";
	
	/** 服务器是否暴露WEBSOCKET */
	public static final String WEBSOCKET_OP = "WEBSOCKET_OP";
	
	/** 服务器是否暴露UDP */
	public static final String UDP_OP = "UDP_OP";
	
	/** 服务器端口常量名 */
	public static final String SERVER_HTTP_PORT = "SERVER_HTTP_PORT";
	/** 服务器端口常量名 */
	public static final String SERVER_HTTP_FILE_PORT = "SERVER_HTTP_FILE_PORT";
	/** 服务器端口常量名 */
	public static final String SERVER_TCP_PORT = "SERVER_TCP_PORT";
	/** 服务器端口常量名 */
	public static final String SERVER_WEBSOCKET_PORT = "SERVER_WEBSOCKET_PORT";
	/** 服务器端口常量名 */
	public static final String SERVER_UDP_PORT = "SERVER_UDP_PORT";
	
	/** spring配置文件地址常量名 */
	public static final String SPRING_FILE_CLASSPATH = "SPRING_FILE_CLASSPATH";
	/** 消息协议映射文件地址常量名 */
	public static final String MESSAGE_CLASS_TO_CODE_MAPPING_FILEPATH = "MESSAGE_CLASS_TO_CODE_MAPPING_FILEPATH";
	/** 业务逻辑执行映射包名常量名 */
	public static final String MESSAGE_CODE_TO_EXECUTOR_MAPPING_PACKAGENAME = "MESSAGE_CODE_TO_EXECUTOR_MAPPING_PACKAGENAME";
	/** 业务逻辑执行线程池最大值 */
	public static final String DISPATCHER_MAX_THREAD_NUM = "DISPATCHER_MAX_THREAD_NUM";
	
	//websocket心跳相关参数
	public static final String WEBSOCKET_HEART_TIMEOUT_COUNT = "WEBSOCKET_HEART_TIMEOUT_COUNT";
	public static final String WEBSOCKET_HEART_CONTEXTS = "WEBSOCKET_HEART_CONTEXTS";
	public static final String WEBSOCKET_HEART_WRITETIMEOUT = "WEBSOCKET_HEART_WRITETIMEOUT";
	
	/** 
	 * Redis
	 */
	
	/** 服务器位置 */
	public static final String REDIS_HOST = "REDIS_HOST";
	/** 服务器端口 */
	public static final String REDIS_PORT = "REDIS_PORT";
	/** 访问口令 */
	public static final String REDIS_AUTH = "REDIS_AUTH";
	
}
