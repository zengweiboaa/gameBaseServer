package com.angke.executor.timetask;

import java.util.concurrent.TimeUnit;

public class TestDemo2 implements TimerTask<Integer> {

	final static Timer timer = new TimerWheel();
	
	
	public static void main(String[] args) {
		TimerTask<Integer> timerTask = new TestDemo2();
		Timeout newTimeout = timer.newTimeout(timerTask, 5000, TimeUnit.MILLISECONDS, 10 );
		
//		long currentTimeMillis = System.currentTimeMillis();
//		System.err.println(currentTimeMillis);
//		TimerTask<Integer> timerTask = new TestDemo2();
//		Timeout newTimeout = null;
//		long currentTimeMillis1 = System.currentTimeMillis();
//		System.err.println(currentTimeMillis1);
//		for (int i = 0; i < 1000000; i++) {
//			newTimeout = timer.newTimeout(timerTask, 5, TimeUnit.MILLISECONDS, i );
//		}
//		long currentTimeMillis2 = System.currentTimeMillis();
//		System.err.println(currentTimeMillis2);
//		newTimeout.cancel();
	}
	@Override
	public void run(Timeout timeout, Integer args) throws Exception {
		Integer arg = (Integer) args;
		System.out.println("timeout, argv = " + arg );
	}

}
