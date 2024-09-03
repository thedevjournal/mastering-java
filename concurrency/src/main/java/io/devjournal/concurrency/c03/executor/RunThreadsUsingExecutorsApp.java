package io.devjournal.concurrency.c03.executor;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class RunThreadsUsingExecutorsApp {

    static final int THREAD_POOL_SIZE = 2;

    public static void main(final String[] args) {

        final String className = RunThreadsUsingExecutorsApp.class.getSimpleName();
        final String threadName = Thread.currentThread().getName();

        final boolean daemon = Math.random() < 0.5;

        final ThreadFactory threadFactory = getThreadFactory(daemon);

        final Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE, threadFactory);

        for (int threadCount = 0; threadCount < THREAD_POOL_SIZE; threadCount++) {
            final Runnable runnable = new GCDRunnable();
            executor.execute(runnable);
        }

        try {
            Thread.sleep(1_000);
        } catch (final InterruptedException e) {
            System.out.println(String.format("[%s][%s] Main Thread Interrupted", className, threadName));
        }

        System.out.println(String.format("[%s][%s] Exiting main()", className, threadName));

        // The main() thread gets terminated, but since User threads keep the JVM
        // active, the Executor also remains active, which in-turn causes the JVM to not
        // shutdown even after all threads are executed

        // There are 3 ways to go about solving this:
        // 1. Use Daemon threads instead of User threads
        // 2. Use ExecutorService.awaitTermination(int, TimeUnit). This gives a fixed
        // duration for threads to execute and the thread pool is shutdown after the
        // duration expires.
        // 3. Add a Runtime hook for Shutdown - Runtime.getRuntime().addShutdownHook();

        if (!daemon) {
            try {
                ((ExecutorService) executor).awaitTermination(10, SECONDS);
                ((ExecutorService) executor).shutdown();
            } catch (final InterruptedException e) {
            }
        }
    }

    static final ThreadFactory getThreadFactory(final boolean daemon) {
        return runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setDaemon(daemon);
            return thread;
        };
    }
}
