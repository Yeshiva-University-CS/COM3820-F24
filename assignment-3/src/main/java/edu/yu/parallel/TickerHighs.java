package edu.yu.parallel;

public interface TickerHighs {
    void addQuoteCount(int count);

    void updateHighestClose(String ticker, double price);

    void updateHighestVolume(String ticker, long volume);

    String getHighestCloseTicker();

    double getHighestClose();

    String getHighestVolumeTicker();

    long getHighestVolume();

    int getQuoteCount();

    // Default implementation of toString
    default String Summary() {
        return String.format("Quotes: %d, Highest Close: (%s, %.2f), Highest Volume: (%s, %d)",
                getQuoteCount(),
                getHighestCloseTicker(), getHighestClose(),
                getHighestVolumeTicker(), getHighestVolume());
    }
}
