package io.devjournal.concurrency.c01.basics;

public class ThreadCreationApp {

    static final int MAX_NUMBER = 10;

    public static void main(final String[] args) {
        runThreadWhichExtendsThreadClass();
        runThreadWhichImplementsRunnableInterface();
        runThreadAnonymousRunnable();
        runThreadUsingLambda();
    }

    static void runThreadWhichExtendsThreadClass() {
        final ExtendingThreadClass thread = new ExtendingThreadClass();
        thread.start();
    }

    static void runThreadWhichImplementsRunnableInterface() {
        final ImplementingRunnableInterface runnable = new ImplementingRunnableInterface();
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    static void runThreadAnonymousRunnable() {
        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                ThreadCreationApp.codeToRun("anonymous-runnable");
            }
        });
        thread.start();
    }

    static void runThreadUsingLambda() {
        final Thread thread = new Thread(() -> ThreadCreationApp.codeToRun("lambda"));
        thread.start();
    }

    static void codeToRun(final String className) {
        int number = 1;

        int sum = 0;

        for (; number <= MAX_NUMBER; number++) {
            sum = sum + number;
        }

        System.out.println(String.format("[%s] Total Sum of Number Upto[%d] => %s", className, MAX_NUMBER, sum));
    }
}
