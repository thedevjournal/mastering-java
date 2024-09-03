package io.devjournal.concurrency.c01.basics;

public class ImplementingRunnableInterface implements Runnable {

    @Override
    public void run() {
        ThreadCreationApp.codeToRun(this.getClass().getSimpleName());
    }
}
