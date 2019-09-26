package com.angke.executor.timetask;

public interface Timeout {

	Timer timer();
    TimerTask<?> task();
    boolean isExpired();
    boolean isCancelled();
    boolean cancel();
}
