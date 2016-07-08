package com.szxb.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {

	/**
	 * @author TangRen
	 * @param args
	 * @time 2016-7-5
	 */

	private static ExecutorService executorService;

	static {
		executorService = Executors.newSingleThreadExecutor();
	}

	public static void executRunable(Runnable command) {
		if (executorService.isShutdown()) {
			executorService.execute(command);
		}

	}

	public static void close() {
		executorService.shutdown();
	}

	class ThreadTest implements Runnable{

		@Override
		public void run() {
			
			
		}

	}

}
