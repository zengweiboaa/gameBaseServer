package com.angke.executor.timetask;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {

	Timeout newTimeout(TimerTask task, long delay, TimeUnit unit, Object argv);
    Set<Timeout> stop();
}
