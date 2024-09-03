package io.devjournal.concurrency.c06.threadpools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class FixedThreadPoolExecutorServiceApp {

    static final int MIN_THREAD_POOL_SIZE = 1;

    static final int MAX_THREAD_POOL_SIZE = 3;

    static final long THREAD_KEEP_ALIVE_DURATION = 1_000;

    static final int WAIT_QUEUE_SIZE = 5;

    // F--
    // Creating the ThreadPoolExecutor Manually to demo purposes. Otherwise, in most cases
    // you should be using Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE)
    static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(
                                                    MIN_THREAD_POOL_SIZE,
                                                    MAX_THREAD_POOL_SIZE,
                                                    THREAD_KEEP_ALIVE_DURATION,
                                                    TimeUnit.MILLISECONDS,
                                                    new ArrayBlockingQueue<Runnable>(WAIT_QUEUE_SIZE),
                                                    new RejectedExecutionHandler() {

                                                        @Override
                                                        public void rejectedExecution(final Runnable task, final ThreadPoolExecutor executor) {
                                                            System.out.println("Task submission Rejected - " + task);
                                                        }
                                                    }
                                               );
    // F++

    public static void main(final String[] args) {
        // F--
        IntStream.rangeClosed(1, 10)
                 .sequential()
                 .forEach(iteration -> {

                     final String taskName = "task-" + iteration;

                     try {
                         Thread.sleep(10);
                         THREAD_POOL.submit(new GCDRunnable(taskName));
                     } catch (final InterruptedException e) {
                         System.out.println("Sleep interrupted for Task - " + taskName);
                     }
                 });
        // F++

        try {

            THREAD_POOL.shutdown();
            System.out.println("THREAD_POOL.shutdown requested. Awaiting 5 seconds for termination");

            THREAD_POOL.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println("THREAD_POOL.awaitTermination completed");

        } catch (final InterruptedException e) {
            System.out.println("THREAD_POOL.awaitTermination interrupted");
        } finally {
            final boolean terminated = THREAD_POOL.isTerminated();
            System.out.println("THREAD_POOL.isTerminated - " + THREAD_POOL.isTerminated());

            if (!terminated) {
                THREAD_POOL.shutdownNow();
                System.out.println("THREAD_POOL.shutdownNow() completed");
            }

            System.out.println("THREAD_POOL termination completed");
        }

        System.out.println("Exiting main()");
    }
}
