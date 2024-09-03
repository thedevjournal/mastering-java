package io.devjournal.concurrency.c05.interrupt;

import java.util.Random;

public class InterruptableGCDRunnable extends Random implements Runnable {

    private static final long serialVersionUID = 935019882899583928L;

    static final String CLASS_NAME = InterruptableGCDRunnable.class.getSimpleName();

    static final int NUMBER_LIMIT = 1_000_000;

    static final int TOTAL_ITERATIONS = 25_000_000;

    static final int OUTPUT_STEP_COUNT = 5_000_000;

    static final String LOG_TEMPLATE = "[%s][%s] GCD of Number1[%d] and Number[%d] => %d";

    @Override
    public void run() {

        final String threadName = getThreadName();

        int number = 1;

        try {
            for (; number <= TOTAL_ITERATIONS; number++) {

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException(threadName);
                }

                final int number1 = nextInt(NUMBER_LIMIT);
                final int number2 = nextInt(NUMBER_LIMIT);

                final int gcd = gcd(number1, number2);

                if (number % OUTPUT_STEP_COUNT == 0) {
                    System.out.println(String.format(LOG_TEMPLATE, CLASS_NAME, threadName, number1, number2, gcd));
                }
            }
        } catch (final InterruptedException e) {
            System.out.println(String.format("[%s][%s] Interrupted", CLASS_NAME, threadName));
        }

        System.out.println(String.format("[%s][%s] Exiting thread.run()", CLASS_NAME, threadName));
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
