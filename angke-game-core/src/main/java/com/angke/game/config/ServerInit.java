package com.angke.game.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.angke.common.config.GlobalConfig;
import com.angke.common.config.MsgConfig;
import com.angke.common.constant.GlobalConfigNames;
import com.angke.common.exception.ConfigException;
import com.angke.common.utils.AssertUtil;
import com.angke.common.utils.SpringContextUtil;

public class ServerInit implements InitializingBean, ApplicationContextAware {

//	public static boolean 
	
	public static boolean OP_PRINT_LOG;
	public static boolean OP_USE_DEFAULT_HANDLER;
	public static boolean OP_PROVIDE_TESTCLIENT;
	
	private static Logger logger = LoggerFactory.getLogger(ServerInit.class);

	// @Autowired
	// private ClusterDao clusterDao;//集群服务

	public static ClassPathXmlApplicationContext loadSpring(String classPathFile) {
		ClassPathXmlApplicationContext actx = 
				new ClassPathXmlApplicationContext(classPathFile);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(actx)));
		return actx;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		// 把applicationContext传入到了SpringContextUtil里面
		SpringContextUtil.setApplicationContext(applicationContext);
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		
		logger.info("----------------------系统初始化开始Service system initialization----------------------");
		
//		MsgConfig.initMsgHelper(ServerConstants.MESSAGECLASS_TO_CODE_CONFIG_FILEPATH, 
//				ServerConstants.MESSAGECODE_TO_EXECUTOR_PACKAGE_NAME, true);
		
		//暂时注释
//		MsgConfig.initMsgHelper("/MsgToCode.properties", "test", true);
		
//		RedisUtils.flushAll();
		
		// initClusterCache();//初始化集群数据 必须最开始完成
		// initRefreshAppStatusTask();
		// initUpdateAppStatusToDB();
		// initUpdateSession();
		loadSystemParam();
		logger.info("--------------系统初始化完成The service system initialization is complete--------------");
		
	}
	// /**
	// * 1.初始化集群数据
	// */
	// private void initClusterCache(){
	// logger.info("1.初始化集群信息到缓存中:ClusterCache开始");
	// //查询数据库所有的集群数据
	// List<ClusterEntity> allClusterInfoList =
	// clusterDao.selectAll(ClusterEntity.class, "delete_flag=0");
	// for (ClusterEntity k8sClusterEntity : allClusterInfoList) {
	// ClusterCache.put(k8sClusterEntity.getUuid() , k8sClusterEntity);//存入缓存
	// }
	// logger.info("1.初始化集群信息到缓存中:ClusterCache完成,总共" + allClusterInfoList.size()
	// + "条数据");
	// }

	// /**
	// * 2.初始化异步任务：刷新所有服务、应用状态
	// */
	// private void initRefreshAppStatusTask() {
	// logger.info("2.初始化任务:RefreshAllAppStatusTask 刷新应用下的k8s的pod状态并存入待更新队列");
	// RefreshAppStatusExcutor.init();
	// logger.info("2.初始化任务:RefreshAllAppStatusTask 完成");
	// }
	//
	// /**
	// * 3.初始化异步任务:更新状态到数据库
	// */
	// private void initUpdateAppStatusToDB() {
	// logger.info("3.初始化任务:RefreshToDBTask 从待更新Appinstance队列取出数据并更新数据库");
	// UpdateAppStatusToDBExcutor.init();
	// logger.info("3.初始化任务:RefreshToDBTask 完成");
	// }
	//
	// /**
	// * 4. 初始化异步任务： 更新用户的所有session
	// */
	// private void initUpdateSession() {
	// logger.info("4.初始化任务：更新session开始");
	// UserSessionUpdateExcutor.init();
	// logger.info("4.初始化任务：更新session结束");
	// }

	////////////////////////////////////////////////////////////////
	
	private void loadSystemParam() {
		String string = GlobalConfig.get(GlobalConfigNames.RUN_MODE);
		if (string == null) {
			string = "demo";
			logger.info("未正确配置服务器启动模式,现在默认以 {} 方式运行", string);
			throw new ConfigException("请配置启动模式项('RUN_MODE')? :'debug' or 'demo' or 'pure'");
		} else {
			logger.info("Server: {} 模式运行中 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ", string);
			if (string.equalsIgnoreCase("debug")) {
				logger.info("'debug' 模式下，请添加自己实现的至少一个业务处理的channelhandler,会打印每一条通信消息的数据内容，提供测试版网页客户端");
				OP_PRINT_LOG = true;
				OP_USE_DEFAULT_HANDLER = false;
				OP_PROVIDE_TESTCLIENT = true;
			} else if (string.equalsIgnoreCase("demo")) {
				logger.info("'demo' 模式下，会打印每一条通信消息的数据内容，提供测试版网页客户端");
				OP_PRINT_LOG = true;
				OP_USE_DEFAULT_HANDLER = true;
				OP_PROVIDE_TESTCLIENT = true;
			} else if (string.equalsIgnoreCase("pure")) {
				logger.info("'pure' 模式下，请添加自己实现的至少一个业务处理的channelhandler,不会打印每一条通信消息的数据内容，不提供测试版网页客户端");
				OP_PRINT_LOG = false;
				OP_USE_DEFAULT_HANDLER = false;
				OP_PROVIDE_TESTCLIENT = false;
			}
		}
	}
	
	////////////////////////////////////////////////////////////////
}
class ShutdownHook implements Runnable {
	
	ClassPathXmlApplicationContext classPathXmlApplicationContext;
	
	public ShutdownHook(ClassPathXmlApplicationContext classPathXmlApplicationContext) {
		AssertUtil.notNull(classPathXmlApplicationContext, "The 'beanfactory' argument must not be null.");
		this.classPathXmlApplicationContext = classPathXmlApplicationContext;
	}
	
	public void run() { // 重写Runnable中的run方法并在此销毁bean
		this.classPathXmlApplicationContext.destroy();
	}
}

