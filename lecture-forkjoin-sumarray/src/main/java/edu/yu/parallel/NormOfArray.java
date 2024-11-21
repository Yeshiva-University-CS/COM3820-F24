package edu.yu.parallel;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NormOfArray {
    private static final Logger logger = LogManager.getLogger(AbstractArrayNorm.class);

    public static void main(String[] args) {

        // If the first argument is not provided, the default value is 100_000_000
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 100_000_000;

        // Print out formatted string with the size of the array using commas
        logger.info("Generating array of size: " + String.format("%,d", size));

        // Generate an array of the specified size using streams
        int[] array = AbstractArrayNorm.generateRandomArray(size, 100);

        int threshold = ForkJoinNorm.DEFAULT_THRESHOLD;

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            // Prompt the user to press the number corresponding to the algorithm
            // they want to run
            logger.info("");
            logger.info("Press 1 to run the sequential algorithm");
            logger.info("Press 2 to run the ForkJoin algorithm (threshold: " + threshold + ")");
            logger.info("Press 3 to run the SequentialStreams algorithm");
            logger.info("Press 4 to run the ParallelStreams algorithm");
            logger.info("Press 5 to exit");
            logger.info("Press 6 to set ForkJoin threshold");

            // Read the user's input
            int choice = scanner.nextInt();

            // Run the algorithm corresponding to the user's input
            // Check that the number is correct, throw an exception if it is not
            switch (choice) {
                case 1:
                    logger.info("Running the Sequential algorithm");
                    SequentialNorm.compute(array);
                    break;
                case 2:
                    logger.info("Running the ForkJoin algorithm");
                    ForkJoinNorm.compute(array, threshold);
                    break;
                case 3:
                    logger.info("Running the SequentialStreams algorithm");
                    SequentialStreamNorm.compute(array);
                    break;
                case 4:
                    logger.info("Running the ParallelStreams algorithm");
                    ParallelStreamNorm.compute(array);
                    break;
                case 5:
                    logger.info("Exiting");
                    running = false;
                    break;
                case 6:
                    logger.info("Enter the new threshold");
                    threshold = scanner.nextInt();
                    break;
                default:
                    logger.error("Invalid choice: " + choice);
            }
        }
        scanner.close();
    }

}
