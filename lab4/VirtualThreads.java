import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class VirtualThreads {
    public static void main(String[] args) throws InterruptedException {
        var counter = new AtomicInteger();

        // Record the start time
        long startTime = System.nanoTime();

        // Create virtual threads
        var threads = IntStream.range(0, 100_000)
                .mapToObj(i -> Thread.ofVirtual().unstarted(() -> {
                    try {
                        Thread.sleep(5_000); // Simulate work
                    } catch (InterruptedException e) {
                        throw new AssertionError(e);
                    }

                    // Increment the counter
                    int currentCount = counter.incrementAndGet();

                    // Print every 10,000th value of the counter
                    if (currentCount % 10_000 == 0) {
                        System.out.println("Counter reached: " + currentCount);
                    }
                }))
                .toList();

        long creationTime = System.nanoTime();
        System.out.println("Thread creation took: " + (creationTime - startTime) / 1_000_000 + " ms");

        // Start the threads and print every 1,000th one
        for (int i = 0; i < threads.size(); i++) {
            var thread = threads.get(i);
            if (i % 1_000 == 0) {
                System.out.println("Starting thread number: " + i);
            }
            thread.start();
        }

        long startThreadsTime = System.nanoTime();
        System.out.println("Thread starting took: " + (startThreadsTime - creationTime) / 1_000_000 + " ms");

        // Wait for all threads to finish
        for (var thread : threads) {
            thread.join();
        }

        long endTime = System.nanoTime();
        System.out.println("Thread execution (including waiting) took: " + (endTime - startThreadsTime) / 1_000_000 + " ms");

        // Total execution time
        System.out.println("Total execution time: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
