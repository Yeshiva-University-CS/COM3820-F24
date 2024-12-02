package edu.yu.parallel;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Interface for processing S&P data files asynchronously.
 * This method allows for non-blocking processing and supports task interruption.
 */
public interface AsyncDataProcessor {

    /**
     * Processes a given CSV file asynchronously and calculates yearly highs for S&P tickers.
     * The method immediately returns a {@code Future} representing the pending computation.
     * <p>
     * The caller can use the {@code Future} to:
     * <ul>
     *     <li>Retrieve the results of the computation when it's complete.</li>
     *     <li>Check the computation status.</li>
     *     <li>Cancel the computation if necessary.</li>
     * </ul>
     * <p>
     * The task supports thread interruption, and any interruption will result
     * in the task being terminated with an {@code InterruptedException}.
     *
     * @param filePath The path to the CSV file.
     * @return A {@code Future} representing the result of the computation.
     * @throws ProcessingException If an error occurs while setting up the task.
     */
    Future<Map<Integer, TickerHighs>> processFileAsync(String filePath) throws ProcessingException;
}
