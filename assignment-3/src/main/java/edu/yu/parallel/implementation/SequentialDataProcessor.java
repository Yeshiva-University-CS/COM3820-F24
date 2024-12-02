package edu.yu.parallel.implementation;

import java.util.Map;

import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerHighs;

public class SequentialDataProcessor implements DataProcessor {

    @Override
    public Map<Integer, TickerHighs> processFile(String filePath) throws ProcessingException {
        // TBD: Implement this method
        // Read the file at the given path and process it sequentially
        // Note: You may NOT use the java.nio package for this implementation
        // Note: You may NOT use the java.util.stream package for this implementation
        throw new ProcessingException("Not yet implemented");
    }
}
