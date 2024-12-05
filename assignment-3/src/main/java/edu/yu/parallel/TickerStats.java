package edu.yu.parallel;

public interface TickerStats {
    /**
     * @return The ticker symbol with the highest close.
     */
    String getHighestCloseTicker();

    /**
     * @return The highest close of any quote across all Tickers.
     */
    double getHighestClose();

    /**
     * @return The ticker symbol with the highest volume.
     */
    String getHighestVolumeTicker();

    /**
     * @return The highest volume of any quote across all Tickers.
     */
    long getHighestVolume();

    /**
     * @return The number of quotes across all Tickers.
     */

    int getQuoteCount();

    // Default implementation of toString
    default String Summary() {
        return String.format("Quotes: %d, Highest Close: (%s, %.2f), Highest Volume: (%s, %d)",
                getQuoteCount(),
                getHighestCloseTicker(), getHighestClose(),
                getHighestVolumeTicker(), getHighestVolume());
    }
}
