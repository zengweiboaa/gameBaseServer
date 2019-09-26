package com.angke.game.model;

public interface BaseBitState {

	public final static boolean IS_ADD_STATE	= true;
	public final static boolean IS_REMOVE_STATE	= false;
	
	
	/** 是否在线 */
	public final static long OP_ONLINE 				= 1L << 0; //连接状态是否在线
	
	
	
	/**
	 * @return 对象当前状态值
	 */
	public long getStatesValue();
	
	/**
	 * 设置状态
	 * @param newValue
	 * @param reSetStatesValue
	 * @return 新的状态对象
	 */
	public BaseBitState setStates(long newValue,boolean reSetStatesValue);
	
	/**
	 * 判断对象当前状态是否包含传入的状态值
	 * @param value  需要判断状态值
	 * @return 是否存在
	 */
	public boolean hasState(long value);
	
	/**
	 * 判断对象当前状态是否包含传入的状态值
	 * @param values
	 * @return
	 */
	public boolean hasState(long...values);
	/**
	 * 为对象添加状态
	 * @param value  需要添加状态值
	 * @return 新的状态对象
	 */
	public BaseBitState addState(long value);

	/**
	 * 批量添加状态值
	 * @param values 需要添加状态值集
	 * @return
	 */
	BaseBitState addState(long...values);
	
	/**
	 * 移除对象的某个状态
	 * @param states 已有状态值
	 * @param value  需要删除状态值
	 * @return 新的状态对象
	 */
	public BaseBitState removeState(long value);

	/**
	 * 批量移除对象的某些状态
	 * @param values 需要删除的状态值集
	 * @return
	 */
	BaseBitState removeState(long...values);

	/**
	 * 值的二进制表示中有多少个1
	 * @return
	 */
	int getBitSize();
	
}
