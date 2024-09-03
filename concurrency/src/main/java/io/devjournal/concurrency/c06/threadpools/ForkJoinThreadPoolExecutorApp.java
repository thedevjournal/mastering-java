package io.devjournal.concurrency.c06.threadpools;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class ForkJoinThreadPoolExecutorApp {

    static final int POOL_SIZE = 2;

    static final List<Integer> NUMBERS = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(final String[] args) {
        runDouble();
        System.out.println("====================================");
        runSum();
    }

    static void runDouble() {
        final ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);
        pool.invoke(new DoubleNumberRecursiveAction(NUMBERS));
    }

    static void runSum() {
        final ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);
        final Integer sum = pool.invoke(new SumNumberRecursiveTask(NUMBERS));
        System.out.println(String.format("Sum of %s -> %s", NUMBERS.toString(), sum));
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

        invokeAll(List.of(leftSplit, rightSplit));
    }
}

class SumNumberRecursiveTask extends RecursiveTask<Integer> {

    private static final long serialVersionUID = -3908927959129267031L;

    static final int FORK_THRESHOLD = 2;

    private final List<Integer> numbers;

    public SumNumberRecursiveTask(final List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    protected Integer compute() {

        final Integer sum;

        if (numbers.size() <= FORK_THRESHOLD) {
            sum = doSum();
        } else {
            sum = split();
        }

        return sum;
    }

    private Integer doSum() {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }

    private Integer split() {

        final int size = numbers.size();

        final int mid = size / 2;

        final SumNumberRecursiveTask leftSplit = new SumNumberRecursiveTask(numbers.subList(0, mid));
        final SumNumberRecursiveTask rightSplit = new SumNumberRecursiveTask(numbers.subList(mid, size));

        // We only fork the rightSplit, because we would like to re-use our current
        // thread to process leftSplit
        rightSplit.fork();

        return leftSplit.compute() + rightSplit.join();
    }
}
