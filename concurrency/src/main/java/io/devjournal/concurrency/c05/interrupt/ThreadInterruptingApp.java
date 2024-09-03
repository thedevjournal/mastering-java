package io.devjournal.concurrency.c05.interrupt;

public class ThreadInterruptingApp {

    public static void main(final String[] args) {

        final boolean interrupt = Math.random() < 0.5;

        final Thread thread = new Thread(new InterruptableGCDRunnable());

        System.out.println("Interrupt GCD Processing - " + interrupt);

        thread.start();

        try {

            Thread.sleep(1_000);

            if (interrupt) {
                System.out.println("Interrupting GCD Processing");
                thread.interrupt();
            }

        } catch (final InterruptedException e) {
            System.out.println("Main Thread Interrupted");
        }

        System.out.println("Exiting main()");
    }
}
