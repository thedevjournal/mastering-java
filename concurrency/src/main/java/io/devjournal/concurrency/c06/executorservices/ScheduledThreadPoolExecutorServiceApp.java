package io.devjournal.concurrency.c06.executorservices;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class ScheduledThreadPoolExecutorServiceApp {

    static final int MIN_THREAD_POOL_SIZE = 2;

    static final int INITIAL_DELAY = 100;

    static final int FIXED_RATE_DELAY = 500;

    static final int FIXED_DELAY = 250;

    static final TimeUnit DELAY_UNIT = TimeUnit.MILLISECONDS;

    public static void main(final String[] args) throws InterruptedException {

        final ScheduledThreadPoolExecutor simpleScheduledThreadPool = getScheduledThreadPoolExecutor();
        final ScheduledThreadPoolExecutor fixedRateScheduledThreadPool = getScheduledThreadPoolExecutor();
        final ScheduledThreadPoolExecutor fixedDelayScheduledThreadPool = getScheduledThreadPoolExecutor();

        final BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> simpleScheduleAction = getSimpleSchedule();
        final BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> fixedRateScheduleAction = getFixedRateSchedule();
        final BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> fixedDelayScheduleAction = getFixedDelaySchedule();

        runInThreadPool(simpleScheduledThreadPool, simpleScheduleAction);
        Thread.sleep(2_000);

        System.out.println("\n\n===========================================\n\n");

        runInThreadPool(fixedRateScheduledThreadPool, fixedRateScheduleAction);
        Thread.sleep(2_000);

        System.out.println("\n\n===========================================\n\n");

        runInThreadPool(fixedDelayScheduledThreadPool, fixedDelayScheduleAction);

        System.out.println("Exiting main()");
    }

    static BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> getSimpleSchedule() {
        return (pool, runnable) -> pool.schedule(runnable, INITIAL_DELAY, DELAY_UNIT);
    }

    static BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> getFixedRateSchedule() {
        return (pool, runnable) -> pool.scheduleAtFixedRate(runnable, INITIAL_DELAY, FIXED_RATE_DELAY, DELAY_UNIT);
    }

    static BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> getFixedDelaySchedule() {
        return (pool, runnable) -> pool.scheduleWithFixedDelay(runnable, INITIAL_DELAY, FIXED_DELAY, DELAY_UNIT);
    }

    static ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {

        // F--
        // Creating the ScheduledThreadPoolExecutor Manually to demo purposes. Otherwise, in most cases
        // you should be using Executors.newScheduledThreadPool(MIN_THREAD_POOL_SIZE)
        // ScheduledThreadPoolExecutor uses a DelayedQueue internally, and all threads have a KEEP_ALIVE of 10ms
        return new ScheduledThreadPoolExecutor(
                MIN_THREAD_POOL_SIZE,
                new RejectedExecutionHandler() {

                    @Override
                    public void rejectedExecution(final Runnable task, final ThreadPoolExecutor executor) {
                        System.out.println("Task submission Rejected - " + task);
                    }
                }
        );
        // F++
    }

    static void runInThreadPool(final ScheduledThreadPoolExecutor threadPool,
            final BiFunction<ScheduledThreadPoolExecutor, Runnable, Future<?>> schedulingAction) {

        // F--
        IntStream.rangeClosed(1, 10)
                 .sequential()
                 .forEach(iteration -> {

                     final String taskName = "task-" + iteration;

                     try {
                         Thread.sleep(100);
                         schedulingAction.apply(threadPool, new GCDRunnable(taskName));
                     } catch (final InterruptedException e) {
                         System.out.println("Sleep interrupted for Task - " + taskName);
                     }
                 });
        // F++

        try {

            threadPool.shutdown();
            System.out.println("THREAD_POOL.shutdown requested. Awaiting 10 seconds for termination");

            threadPool.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println("THREAD_POOL.awaitTermination completed");

        } catch (final InterruptedException e) {
            System.out.println("THREAD_POOL.awaitTermination interrupted");
        } finally {
            final boolean terminated = threadPool.isTerminated();
            System.out.println("THREAD_POOL.isTerminated - " + threadPool.isTerminated());

            if (!terminated) {
                threadPool.shutdownNow();
                System.out.println("THREAD_POOL.shutdownNow() completed");
            }

            System.out.println("THREAD_POOL termination completed");
        }
    }
}
