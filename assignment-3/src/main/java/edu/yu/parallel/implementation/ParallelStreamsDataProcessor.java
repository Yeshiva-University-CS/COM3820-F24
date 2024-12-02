package edu.yu.parallel.implementation;

import java.io.IOException;
import java.util.Map;
import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerStats;

public class ParallelStreamsDataProcessor implements DataProcessor {

    @Override
    public Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException {
        // TBD: Implement this method using parallel streams
        // This method should read the file at the given path and process it using streams
        throw new ProcessingException("Not yet implemented");
    }
}
