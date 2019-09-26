package com.angke.executor.timetask;

public interface TimerTask<T> {

	void run(Timeout timeout, T args) throws Exception;
}
