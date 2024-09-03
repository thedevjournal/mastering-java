package io.devjournal.concurrency.c04.threadjoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchInListUsingThreadsApp {

    // F--
    static final List<String> WORDS = Arrays.asList(
                                        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I", "it",
                                        "for", "not", "on", "with", "he", "as", "you", "do", "at", "this",
                                        "but", "his", "by", "from", "they", "we", "say", "her", "she", "or",
                                        "an", "will", "my", "one", "all", "would", "there", "their", "what",
                                        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
                                        "when", "make", "can", "like", "time", "no", "just", "him", "know",
                                        "take", "people", "into", "year", "your", "good", "some", "could",
                                        "them", "see", "other", "than", "then", "now", "look", "only", "come",
                                        "its", "over", "think", "also", "back", "after", "use", "two", "how",
                                        "our", "work", "first","well", "way", "even", "new", "want", "because",
                                        "any", "these", "give", "day", "most", "us");

    static final List<String> SEARCH = Arrays.asList(
                                            "forest", "universe", "technology","galaxy", "the", "and",
                                            "in", "for", "on", "orange", "sky", "river", "mountain",
                                            "with", "by", "at", "one", "computer", "from", "apple");
    // F++

    public static void main(final String[] args) {

        final List<Thread> searchThreads = new ArrayList<Thread>(SEARCH.size());

        // Create all threads and store them in the Thread List
        for (int index = 0; index < SEARCH.size(); index++) {

            final String wordToSearch = SEARCH.get(index);

            final WordSearchRunnable runnable = new WordSearchRunnable(WORDS, wordToSearch);

            final Thread thread = new Thread(runnable, "WordSearch-" + index);

            searchThreads.add(thread);

            thread.start();
        }

        final String className = SearchInListUsingThreadsApp.class.getSimpleName();
        final String threadName = Thread.currentThread().getName();

        // Now, join() all threads to the main thread so that we wait for all our search
        // threads to complete, before exiting our main() thread
        for (final Thread thread : searchThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                System.out.println(String.format("[%s][%s] Thread join() interrupted", className, threadName));
                e.printStackTrace();
            }
        }

        System.out.println(String.format("[%s][%s] main() completed", className, threadName));
    }
}

class WordSearchRunnable implements Runnable {

    static final String LOG_FORMAT = "[%s][%s] Found Word[%s] at Index[%d]";

    private final List<String> words;

    private final int wordsLength;

    private final String wordToSearch;

    public WordSearchRunnable(final List<String> words, final String wordToSearch) {
        this.words = words;
        this.wordsLength = words.size();
        this.wordToSearch = wordToSearch;
    }

    @Override
    public void run() {

        final String className = getClass().getSimpleName();
        final String threadName = Thread.currentThread().getName();

        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            System.out.println(String.format("[%s][%s] sleep() interrupted", className, threadName));
        }

        int index = 0;

        for (; index < wordsLength; index++) {

            final String currentWord = words.get(index);

            if (currentWord.equals(wordToSearch)) {
                System.out.println(String.format(LOG_FORMAT, className, threadName, wordToSearch, index));
            }
        }

        System.out.println(String.format("[%s][%s] run() completed", className, threadName));
    }
}
