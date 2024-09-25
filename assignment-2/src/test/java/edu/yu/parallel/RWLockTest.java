package edu.yu.parallel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RWLockTest {
    private final static Logger logger = LogManager.getLogger(RWLockTest.class);

    static {
        Configurator.setLevel("edu.yu.parallel", Level.INFO);
    }

    private ExecutorService executor;
    private RWLockInterface rwLock;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(15);
        rwLock = new RWLock();
    }


    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Nested
    @DisplayName("Basic lock/unlock use cases")
    class BasicLockUnlockUseCases {

        @Test
        @DisplayName("Unlock works when I have the read lock")
        public void unlockWorksWhenIHaveTheReadLock() throws InterruptedException {
            final var lock = new RWLock();
            lock.lockRead();
            lock.unlock();
        }

        @Test
        @DisplayName("Unlock works when I have the write lock")
        public void unlockWorksWhenIHaveTheWriteLock() throws InterruptedException {
            rwLock.lockWrite();
            rwLock.unlock();
        }

        @Test
        @DisplayName("Can't unlock w/o lock, when no one has the lock")
        public void cantUnlockWhenNobodyHasLock() {
            Assertions.assertThrows(IllegalMonitorStateException.class, () -> {
                rwLock.unlock();
            });
        }
    }

    @Nested
    @DisplayName("MT locked for read scenario")
    class StressTest {

        /**
         * Stress test for synchronization issues
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Test
        @DisplayName("Stress test lock synchronization")
        public void lockSyncStressTest() throws InterruptedException, ExecutionException {
            final AtomicLong readers = new AtomicLong();
            final AtomicLong writers = new AtomicLong();

            class LockState {
                LockState(long readerCount, long writerCount) { this.readerCount = readerCount; this.writerCount = writerCount;}
                boolean error;
                long readerCount;
                long writerCount;
            }

            Callable readerTask = () -> {
                rwLock.lockRead();
                try {
                    long readerCount = readers.incrementAndGet();
                    long writerCount = writers.get();
                    var state = new LockState(readerCount, writerCount);
                    state.error = writerCount > 0;
                    TestUtils.SleepRandomTime(10);
                    return state;
                }
                finally {
                    readers.decrementAndGet();
                    rwLock.unlock();
                }
            };

            Callable writerTask = () -> {
                rwLock.lockWrite();
                try {
                    long writerCount = writers.incrementAndGet();
                    long readerCount = readers.get();
                    var state = new LockState(readerCount, writerCount);
                    state.error = writerCount > 1 || readerCount > 0;
                    TestUtils.SleepRandomTime(10);
                    return state;
                }
                finally {
                    writers.decrementAndGet();
                    rwLock.unlock();
                }
            };

            final int processors = Runtime.getRuntime().availableProcessors();
            final var exec = Executors.newFixedThreadPool(processors);
            final var service = new ExecutorCompletionService<LockState>(exec);

            final int numberOfTasks =  1000 * 1;
            IntStream.range(0, numberOfTasks)
                    .map(x -> new Random().nextInt(2))
                    .mapToObj(x -> (x == 0) ? writerTask : readerTask)
                    .forEach(task -> service.submit(task));

            int errorCount = 0;
            for (int i=0; i < numberOfTasks; ++i) {
                var state = service.take().get();
                if (state.error) {
                    errorCount++;
                    logger.error("Lock state error: Readers={}, Writer={}", state.readerCount, state.writerCount);
                }
            }

            assertEquals(0, errorCount);
        }

    }
}