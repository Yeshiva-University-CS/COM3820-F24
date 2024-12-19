import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class VirtualExecutor {
    public static void main(String[] args) throws InterruptedException {
        var counter = new AtomicInteger();

        // Record the start time
        long startTime = System.nanoTime();

        // Use a virtual thread per task executor
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Submit tasks to the executor and print every 1,000th task
            IntStream.range(0, 100_000).forEach(i -> {
                if (i % 1_000 == 0) {
                    System.out.println("Submitting task number: " + i);
                }
                executor.execute(() -> {
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
                });
            });

            long submissionTime = System.nanoTime();
            System.out.println("Task submission took: " + (submissionTime - startTime) / 1_000_000 + " ms");

            // Executor is automatically shut down and waits for all tasks to complete at the end of try-with-resources
        }

        long endTime = System.nanoTime();
        System.out.println("Task execution (including waiting) took: " + (endTime - startTime) / 1_000_000 + " ms");

        // Total execution time
        System.out.println("Total execution time: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
