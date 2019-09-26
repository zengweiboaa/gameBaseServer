package com.angke.game.io.transport;

public class HeartContentJson{
	private int timeoutCount;
	private long timeout;
	public HeartContentJson(int timeoutCount, long timeout) {
		this.timeoutCount = timeoutCount;
		this.timeout = timeout;
	}
	public int getTimeoutCount() {
		return timeoutCount;
	}
	public void setTimeoutCount(int timeoutCount) {
		this.timeoutCount = timeoutCount;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	@Override
	public String toString() {
		return "HeartContentJson [timeoutCount=" + timeoutCount + ", timeout=" + timeout + "]";
	}
	
}