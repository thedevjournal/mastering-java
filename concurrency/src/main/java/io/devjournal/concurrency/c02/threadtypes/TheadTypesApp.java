package io.devjournal.concurrency.c02.threadtypes;

import java.util.Random;

public class TheadTypesApp {

    static final int NUMBER_LIMIT = 1_000_000;

    static final int TOTAL_ITERATIONS = 25_000_000;

    static final int OUTPUT_STEP_COUNT = 5_000_000;

    static final Random RANDOM = new Random();

    static final String LOG_TEMPLATE = "[%s] GCD of Number1[%d] and Number[%d] => %d";

    public static void codeToRun(final String className) {

        int number = 1;

        for (; number <= TOTAL_ITERATIONS; number++) {

            final int number1 = RANDOM.nextInt(NUMBER_LIMIT);
            final int number2 = RANDOM.nextInt(NUMBER_LIMIT);

            final int gcd = gcd(number1, number2);

            if (number % OUTPUT_STEP_COUNT == 0) {
                System.out.println(String.format(LOG_TEMPLATE, className, number1, number2, gcd));
            }
        }
    }

    static int gcd(final int number1, final int number2) {

        final int gcd;

        if (number2 == 0) {
            gcd = number1;
        } else {
            gcd = gcd(number2, number1 % number2);
        }

        return gcd;
    }

    public static void main(final String[] args) {

        final boolean daemon = Math.random() < 0.5;

        final Thread thread = daemon ? new DaemonThread() : new UserThread();

        thread.start();

        try {
            Thread.sleep(1_000);
        } catch (final InterruptedException e) {
            System.out.println("Main Thread Interrupted");
        }

        System.out.println("Exiting main()");
    }
}

class UserThread extends Thread {

    @Override
    public void run() {
        TheadTypesApp.codeToRun(getClass().getSimpleName());
        System.out.println(String.format("[%s] Exiting thread.run()", getClass().getSimpleName()));
    }
}

class DaemonThread extends Thread {

    public DaemonThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
        TheadTypesApp.codeToRun(getClass().getSimpleName());
        System.out.println(String.format("[%s] Exiting thread.run()", getClass().getSimpleName()));
    }
}
