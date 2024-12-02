package edu.yu.parallel.implementation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import edu.yu.parallel.AsyncDataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerHighs;

public class ParallelStreamsDataProcessor implements AsyncDataProcessor {

    @Override
    public Future<Map<Integer, TickerHighs>> processFileAsync(String filePath) throws ProcessingException {
        // TBD: Implement this method using parallel streams
        // This method should read the file at the given path and process it using streams
        throw new ProcessingException("Not yet implemented");
    }
}
