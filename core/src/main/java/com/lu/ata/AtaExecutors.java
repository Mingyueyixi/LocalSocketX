package com.lu.ata;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lu
 * @date 2024/12/9 0:03
 * @description
 */
public class AtaExecutors {
    private static ExecutorService sExecutor = new ThreadPoolExecutor(3, 12,
            20L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    private static Executor sMainThreadExecutor = new Executor() {
        private Handler mainHandler;

        @Override
        public void execute(Runnable command) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                command.run();
            } else {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
                mainHandler.post(command);
            }
        }
    };

    public static void executeNetwork(Runnable runnable) {
        sExecutor.submit(runnable);
    }

    public static void executeMain(Runnable runnable) {
        sMainThreadExecutor.execute(runnable);
    }
}
