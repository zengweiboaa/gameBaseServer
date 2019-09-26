package com.angke.game.model.container;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.angke.game.model.GlobalId;

public interface Group<R> extends Set<R>, Comparable<Group<R>> {

	/**
	 * 该容器组的名字
	 * @return
	 */
	String name();
	
	/**
	 * 查找到某个具体id的对象
	 * @return
	 */
	R find(GlobalId id);
	
	/**
	 * 找出所有某个类型的对象
	 * @param clz
	 * @return
	 */
	ConcurrentMap<GlobalId, R> find(Class<? extends R> clz);
	
	/**
	 * 所有某个类型的对象的个数
	 * @param clz
	 * @return
	 */
	int size(Class<? extends R> clz);
	
}
