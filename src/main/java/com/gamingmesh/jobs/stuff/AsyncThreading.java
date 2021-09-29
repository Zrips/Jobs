package com.gamingmesh.jobs.stuff;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncThreading {

    public static final ExecutorService ASYNC_POOL = Executors.newScheduledThreadPool(500, new ThreadFactory() {
        final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, String.format("Thread %s", counter.incrementAndGet()));
        }
    });

    public static void run(Runnable r) {
        ASYNC_POOL.execute(r);
    }
}
