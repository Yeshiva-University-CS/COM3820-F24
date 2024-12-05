package edu.yu.parallel;

import java.io.IOException;
import java.util.Map;

public interface DataProcessor {
    /**
     * Processes a given CSV file and calculates yearly highs.
     *
     * @param filePath The path to the CSV file.
     * @return A map where the key is the year, and the value is a TickerStats object representing the statistics for that year.
     * @throws ProcessingException If an error occurs while reading or processing the file.
     */
    Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException;
}
