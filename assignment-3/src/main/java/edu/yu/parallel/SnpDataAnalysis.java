package edu.yu.parallel;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.parallel.implementation.ParallelDataProcessor;
import edu.yu.parallel.implementation.ParallelStreamsDataProcessor;
import edu.yu.parallel.implementation.SequentialDataProcessor;

public class SnpDataAnalysis {
    private final static Logger logger = LogManager.getLogger(SnpDataAnalysis.class);
    private final AsyncDataProcessor processor;
    private final String processorName;

    public SnpDataAnalysis(AsyncDataProcessor processor) {
        this.processor = processor;
        processorName = processor.getClass().getSimpleName();
    }

    public SnpDataAnalysis(DataProcessor processor) {
        processorName = processor.getClass().getSimpleName();

        // Wrap the processor in an async processor
        // so that our code below can be uniform
        this.processor = filePath -> {
            FutureTask<Map<Integer, TickerHighs>> futureTask = new FutureTask<>(
                    () -> processor.processFile(filePath));
            futureTask.run();
            return futureTask;
        };
    }

    public void analyze(String filePath) {
        long startTime = 0L, endTime = 0L;
        logger.info("Analyzing file: {}", filePath);
        logger.info("Processor: {}", processorName);
        try {
            startTime = System.nanoTime();
            var resultFuture = processor.processFileAsync(filePath);
            var resultMap = resultFuture.get();
            endTime = System.nanoTime();

            // Print results
            for (Map.Entry<Integer, TickerHighs> entry : resultMap.entrySet()) {
                int year = entry.getKey();
                var highs = entry.getValue();
                System.out.printf("%d: %s%n", year, highs.Summary());
            }
        } catch (Exception e) {
            endTime = System.nanoTime();
            var cause = e.getCause() == null ? e : e.getCause();
            logger.error("{}: {}", cause.getClass().getName(), cause.getMessage());
        }
        logger.info("Analysis complete in {} ms", (endTime - startTime) / 1_000_000);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java SnpDataAnalysis <csv_file_path>");
            return;
        }

        String filePath = args[0];

        DataProcessor sequentialProcessor = new SequentialDataProcessor();
        SnpDataAnalysis analysis = new SnpDataAnalysis(sequentialProcessor);

        AsyncDataProcessor parallelProcessor = new ParallelDataProcessor();
        SnpDataAnalysis analysis2 = new SnpDataAnalysis(parallelProcessor);

        AsyncDataProcessor parallelStreamsProcessor = new ParallelStreamsDataProcessor();
        SnpDataAnalysis analysis3 = new SnpDataAnalysis(parallelStreamsProcessor);

        // Run the analysis
        analysis.analyze(filePath);
        analysis2.analyze(filePath);
        analysis3.analyze(filePath);
    }
}
