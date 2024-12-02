package edu.yu.parallel;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.xml.crypto.Data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.parallel.implementation.ParallelDataProcessor;
import edu.yu.parallel.implementation.ParallelStreamsDataProcessor;
import edu.yu.parallel.implementation.SequentialDataProcessor;

public class SnpDataAnalysis {
    private final static Logger logger = LogManager.getLogger(SnpDataAnalysis.class);
    private final DataProcessor processor;

    public SnpDataAnalysis(DataProcessor processor) {
        this.processor = processor;
    }

    public void analyze(String filePath) {
        long startTime = 0L, endTime = 0L;
        logger.info("Analyzing file: {}", filePath);
        logger.info("Processor: {}", processor.getClass().getSimpleName());
        try {
            startTime = System.nanoTime();
            var resultMap = processor.processFile(filePath);
            endTime = System.nanoTime();

            // Print results in order of year
            Map<Integer, TickerStats> sortedMap = new TreeMap<>(resultMap);
            for (Map.Entry<Integer, TickerStats> entry : sortedMap.entrySet()) {
                int year = entry.getKey();
                var highs = entry.getValue();
                System.out.printf("%d: %s%n", year, highs.Summary());
            }
        } catch (Exception e) {
            endTime = System.nanoTime();
            var cause = e.getCause() == null ? e : e.getCause();
            logger.error("{}: {}", cause.getClass().getSimpleName(), cause.getMessage());
        }
        logger.info("Analysis complete in {} ms", (endTime - startTime) / 1_000_000);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java SnpDataAnalysis <csv_file_path>");
            return;
        }

        String filePath = args[0];

        DataProcessor[] processors = {
                new SequentialDataProcessor(),
                new ParallelDataProcessor(),
                new ParallelStreamsDataProcessor()
        };

        for (DataProcessor processor : processors) {
            logger.info("----- Begin: {} -----", processor.getClass().getSimpleName());
            SnpDataAnalysis analysis = new SnpDataAnalysis(processor);
            analysis.analyze(filePath);
        }
    }
}
