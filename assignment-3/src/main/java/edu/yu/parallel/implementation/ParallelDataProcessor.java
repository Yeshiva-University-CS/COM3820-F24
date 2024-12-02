package edu.yu.parallel.implementation;

import java.io.IOException;
import java.util.Map;
import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerStats;

public class ParallelDataProcessor implements DataProcessor {

    @Override
    public Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException {
        // TBD: Implement this method
        // Read the file at the given path and process it in parallel
        // Note: You may NOT use the java.nio package for this implementation
        // Note: You may NOT use the java.util.stream package for this implementation
        throw new ProcessingException("Not yet implemented");
    }
}
