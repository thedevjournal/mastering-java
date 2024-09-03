package io.devjournal.concurrency.c06.threadpools;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinThreadPoolExecutorApp {

    static final int POOL_SIZE = 2;

    public static void main(final String[] args) {

        final ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);

        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        pool.invoke(new DoubleNumberRecursiveAction(numbers));
    }
}

class DoubleNumberRecursiveAction extends RecursiveAction {

    private static final long serialVersionUID = -3908927959129267031L;

    static final int FORK_THRESHOLD = 2;

    private final List<Integer> numbers;

    public DoubleNumberRecursiveAction(final List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    protected void compute() {
        if (numbers.size() <= FORK_THRESHOLD) {
            doDouble();
        } else {
            split();
        }
    }

    private void doDouble() {
        final String threadName = Thread.currentThread().getName();
        numbers.stream().forEach(number -> System.out.println(String.format("[%s] Double of %d -> %d", threadName, number, number * 2)));
    }

    private void split() {

        final int size = numbers.size();

        final int mid = size / 2;

        final DoubleNumberRecursiveAction leftSplit = new DoubleNumberRecursiveAction(numbers.subList(0, mid));
        final DoubleNumberRecursiveAction rightSplit = new DoubleNumberRecursiveAction(numbers.subList(mid, size));

        invokeAll(Arrays.asList(leftSplit, rightSplit));
    }
}
