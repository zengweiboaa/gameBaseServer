package com.angke.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 注解业务执行器消息码
 * @author LiangShengxian
 *
 */
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgToExec {

	short msgType();
}