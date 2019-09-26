package com.angke.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class SpringContextUtil {

	private static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

	// @Autowired 沿用springTest的这种方法 是否会更好？
	// ApplicationContext ctx;
	private static ApplicationContext applicationContext = null;

	public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		logger.info("------SpringContextUtil setApplicationContext-------");
		SpringContextUtil.applicationContext = applicationContext;
	}

	// 变成静态方法，用static修饰一下
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型. bean的name如果在注解的时候没有指定，默认 =
	 * 类名(首字母小写) 例如: XxxxService = getBean("xxxxService")
	 * 
	 * @param name
	 * @return
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 根据类名获取到bean
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) throws BeansException {
		checkApplicationContext();
		try {
			char[] cs = clazz.getSimpleName().toCharArray();
			cs[0] += 32;// 首字母大写到小写
			return (T) applicationContext.getBean(String.valueOf(cs));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.isSingleton(name);
	}

	private static void checkApplicationContext() {
		Assert.notNull(applicationContext, "applicaitonContext未注入,请在applicationContext.xml中定义SpringContextUtil");
	}
}
