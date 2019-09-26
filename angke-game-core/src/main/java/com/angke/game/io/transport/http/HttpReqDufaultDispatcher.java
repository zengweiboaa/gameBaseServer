//package com.angke.game.io.transport.http;
//
//import java.lang.reflect.Field;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.apache.log4j.Logger;
//
//import com.angke.tianfu.core.common.constants.GlobalConstants.ServerConstants;
//import com.angke.tianfu.core.model.util.MsgHelper;
//import com.angke.tianfu.server.control.executor.BaseBusinessLogicExecutor;
//import com.google.protobuf.MessageLite;
//
//import io.netty.channel.Channel;
//
//public class HttpReqDufaultDispatcher {
//
//	private static final Logger logger = Logger.getLogger(HttpReqDufaultDispatcher.class);
//	
//	private static ExecutorService executorService =
//			Executors.newFixedThreadPool(ServerConstants.DISPATCHER_MAX_THREAD_NUM);
//	/**
//	 * 提交到线程次
//	 * @param channel
//	 * @param msg
//	 * @throws InstantiationException
//	 * @throws IllegalAccessException
//	 */
//	public static void submit(Channel channel, MessageLite msg) 
//			throws InstantiationException, IllegalAccessException {
//		
//		Field[] fields = msg.getClass().getFields();
//		logger.info("消息到达Dispatcher:");
//		logger.info(fields);
//		System.err.println(msg.getClass());
//		logger.info("Dispatcher:开始分发处理器");
//		
//		Class<?> executorClass = MsgHelper
//				.getExecutorClassByMsgCode(MsgHelper.getMsgCodeByMsgClass(msg.getClass()));
//		logger.info("取得执行器类型 " + executorClass);
//		BaseBusinessLogicExecutor executor = (BaseBusinessLogicExecutor) executorClass.newInstance();
//		logger.info("取得执行器实例 " + executor);
//		executor.setChannel(channel);
//		executor.setRequestMsg(msg);
//
//		executorService.submit(executor);
//	}
//}
