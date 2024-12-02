package edu.yu.parallel.implementation;

import java.util.Map;
import java.util.concurrent.*;

import edu.yu.parallel.AsyncDataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerHighs;

public class ParallelDataProcessor implements AsyncDataProcessor {

    @Override
    public Future<Map<Integer, TickerHighs>> processFileAsync(String filePath) throws ProcessingException {
        // TBD: Implement this method
        // Read the file at the given path and process it in parallel
        // Note: You may NOT use the java.nio package for this implementation
        // Note: You may NOT use the java.util.stream package for this implementation
        throw new ProcessingException("Not yet implemented");
    }
}
