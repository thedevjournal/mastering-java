package io.devjournal.concurrency.c06.threadpools;

import java.util.Random;

public class GCDRunnable extends Random implements Runnable {

    private static final long serialVersionUID = 935019882899583928L;

    static final String CLASS_NAME = GCDRunnable.class.getSimpleName();

    static final int NUMBER_LIMIT = 1_000_000;

    static final int TOTAL_ITERATIONS = 25_000_000;

    static final int OUTPUT_STEP_COUNT = 5_000_000;

    static final String LOG_TEMPLATE = "[%s][%s] GCD of Number1[%d] and Number[%d] => %d";

    private final String taskName;

    public GCDRunnable(final String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {

        final String threadName = getThreadName();
        final String className = CLASS_NAME + "-" + taskName;

        int number = 1;

        for (; number <= TOTAL_ITERATIONS; number++) {

            final int number1 = nextInt(NUMBER_LIMIT);
            final int number2 = nextInt(NUMBER_LIMIT);

            final int gcd = gcd(number1, number2);

            if (number % OUTPUT_STEP_COUNT == 0) {
                System.out.println(String.format(LOG_TEMPLATE, className, threadName, number1, number2, gcd));
            }
        }

        System.out.println(String.format("[%s][%s] Exiting thread.run()", className, threadName));
    }

    @Override
    public String toString() {
        return String.format("%s(taskName = %s)", CLASS_NAME, taskName);
    }

    public String getTaskName() {
        return taskName;
    }

    private int gcd(final int number1, final int number2) {

        final int gcd;

        if (number2 == 0) {
            gcd = number1;
        } else {
            gcd = gcd(number2, number1 % number2);
        }

        return gcd;
    }

    private String getThreadName() {

        final boolean daemon = Thread.currentThread().isDaemon();
        final String threadNamePrefix = daemon ? "Daemon" : "User";

        return threadNamePrefix + Thread.currentThread().getName();
    }
}
