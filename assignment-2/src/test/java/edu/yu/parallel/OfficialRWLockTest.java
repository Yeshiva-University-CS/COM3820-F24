package edu.yu.parallel;

import edu.yu.parallel.execution.ControlledExecution;
import edu.yu.parallel.execution.ExecutionGroup;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

class OfficialRWLockTest {
    private static final Logger logger = LogManager.getLogger(OfficialRWLockTest.class);
    private static final long STD_WAIT_TIME = 200L;

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
        public void unlockWorksWhenIHaveTheReadLock() {
            final var lock = new RWLock();
            lock.lockRead();
            lock.unlock();
        }

        @Test
        @DisplayName("Unlock works when I have the write lock")
        public void unlockWorksWhenIHaveTheWriteLock() {
            rwLock.lockWrite();
            rwLock.unlock();
        }

        @Test
        @DisplayName("Can't unlock w/o lock, when no one has the lock")
        public void cantUnlockWhenNobodyHasLock() {
            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

        @Test
        @DisplayName("Can't unlock w/o lock, when another has the write lock")
        public void cantUnlockWhenSomeoneElseHasWriteLock() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createWriterTask());
            group.awaitReadyToLock();
            group.lockInOrder();
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));

            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

        @Test
        @DisplayName("Can't unlock w/o lock, when another has the read lock")
        public void cantUnlockWhenSomeoneElseHasReadLock() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createReaderTask());
            group.awaitReadyToLock();
            group.lockInOrder();
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));

            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

    }

    @Nested
    @DisplayName("MT locked for write scenarios")
    class MultiThreadLockedForWriteScenarios {
        /**
         * Verifies that write lock blocks subsequent write requests.
         * AND that queued write threads are woken up one by one after the write lock is
         * released
         */
        @Test
        @DisplayName("WWW - Write blocks subsequent writes from getting the lock")
        public void writeLockBlocksWriteRequests() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createWriterTask());
            executor.submit(group.createWriterTask());
            executor.submit(group.createWriterTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Test that subsequent writes wait
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));

            // Test that waiters writers are woken up and get the lock in order
            group.completeExecution(0);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));

            group.completeExecution(1);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(2));
        }

        /**
         * Verifies that write lock blocks subsequent reads,
         * AND that queued readers threads are *all* woken up after the write lock is
         * released
         */
        @Test
        @DisplayName("WRRRRRRRRR - Write blocks many subsequent reads")
        public void writeBlocksManyReadRequests() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            int reads = 9;

            executor.submit(group.createWriterTask());
            for (int i = 0; i < reads; i++)
                executor.submit(group.createReaderTask());

            group.awaitReadyToLock();
            group.lockInOrder();
            group.completeExecution(0);

            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            for (int i = 0; i < reads; i++)
                Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(i + 1));
        }

        /**
         * Verifies that write lock blocks subsequent reads,
         * AND that queued readers threads are *all* woken up after the write lock is
         * released
         */
        @Test
        @DisplayName("WRR - Write blocks subsequent reads")
        public void manyBlockedReadRequests() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createWriterTask());
            executor.submit(group.createReaderTask());
            executor.submit(group.createReaderTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Test that reads are blocked
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));

            // Test that reads are both woken up when writer is unlocked
            group.completeExecution(0);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(2));
        }

        /**
         * Verifies that a waiting write request is queued after a previously waiting
         * read request
         */
        @Test
        @DisplayName("WRW - Pending write request obtains the lock after previous pending read request")
        public void writeReadWrite() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createWriterTask());
            executor.submit(group.createReaderTask());
            executor.submit(group.createWriterTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Verify the initial state
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));

            // Test that reads are both woken up when writer is unlocked
            group.completeExecution(0);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));
        }

        /**
         * Verifies that a read request is queued after the last queued write request
         * and does not try to leap over it and join a previous pending read group.
         */
        @Test
        @DisplayName("WRWR - Pending read is queued after the last pending write")
        public void writeReadWriteRead() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createWriterTask());
            executor.submit(group.createReaderTask());
            executor.submit(group.createWriterTask());
            executor.submit(group.createReaderTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Verify the initial state
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(3));

            // Test that the second read is still waiting after the pending reads and writes
            // got the lock
            group.completeExecution(0);
            group.completeExecution(1);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(2));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(3));
        }
    }

    @Nested
    @DisplayName("MT locked for read scenario")
    class MultiThreadLockedForReadScenarios {
        /**
         * Verifies that a read lock blocks a subsequent write request from being able
         * to lock
         * AND write request is automatically woken up and receives the lock when the
         * read releases its lock
         */
        @Test
        @DisplayName("RW - Read lock blocks write from getting lock")
        public void readLockBlocksWriteRequest() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createReaderTask());
            executor.submit(group.createWriterTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Verify the initial state
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(1));

            group.completeExecution(0);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
        }

        /**
         * Verifies that multiple threads can read concurrently
         * AND can unlock one without the other
         */
        @Test
        @DisplayName("RRR - Multiple threads can read concurrently")
        public void multipleThreadsCanReadConcurrently() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createReaderTask());
            executor.submit(group.createReaderTask());
            executor.submit(group.createReaderTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Verify that threads can lock concurrently
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(2));

            // Verify that each thread can be unlocked individually
            group.completeExecution(0);
            group.completeExecution(2);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(2));
        }

        /**
         * Verifies that a queued write request will not get the lock until *all*
         * concurrent reader locks are released
         * AND that the queued writer thread is woken up automatically after the read
         * locks are released
         */
        @Test
        @DisplayName("RRW - Write can obtain lock only after all readers complete")
        public void lockForWriteOnlyAfterAllReadersComplete() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createReaderTask());
            executor.submit(group.createReaderTask());
            executor.submit(group.createWriterTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Verify that threads can lock concurrently
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));

            // Verify that the write thread will not get the lock until all readers are done
            group.completeExecution(0);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));

            group.completeExecution(1);
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.UNLOCKED, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(2));
        }

        /**
         * Verifies that a pending write blocks a read request, even if there are other
         * readers that currently have the lock
         */
        @Test
        @DisplayName("RWR - Pending write blocks a read")
        public void pendingWriteBlocksRead() {
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);

            executor.submit(group.createReaderTask());
            executor.submit(group.createWriterTask());
            executor.submit(group.createReaderTask());

            group.awaitReadyToLock();
            group.lockInOrder();

            // Verify the second read is not given the lock along with the current read
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(1));
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(2));
        }
    }

    @Nested
    @DisplayName("MT locck stress test")
    class StressTest {
        /**
         * Stress test for synchronization issues
         */
        @Test
        @DisplayName("Stress test lock synchronization")
        public void lockSyncStressTest() throws InterruptedException, ExecutionException {
            final AtomicLong readers = new AtomicLong();
            final AtomicLong writers = new AtomicLong();

            class LockState {
                LockState(long readerCount, long writerCount) {
                    this.readerCount = readerCount;
                    this.writerCount = writerCount;
                }

                boolean error;
                final long readerCount;
                final long writerCount;
            }

            Callable<LockState> readerTask = () -> {
                rwLock.lockRead();
                try {
                    long readerCount = readers.incrementAndGet();
                    long writerCount = writers.get();
                    var state = new LockState(readerCount, writerCount);
                    state.error = writerCount > 0;
                    TestUtils.SleepRandomTime(10);
                    return state;
                } finally {
                    readers.decrementAndGet();
                    rwLock.unlock();
                }
            };

            Callable<LockState> writerTask = () -> {
                rwLock.lockWrite();
                try {
                    long writerCount = writers.incrementAndGet();
                    long readerCount = readers.get();
                    var state = new LockState(readerCount, writerCount);
                    state.error = writerCount > 1 || readerCount > 0;
                    TestUtils.SleepRandomTime(10);
                    return state;
                } finally {
                    writers.decrementAndGet();
                    rwLock.unlock();
                }
            };

            final int processors = Runtime.getRuntime().availableProcessors();
            final var exec = Executors.newFixedThreadPool(processors);
            final var service = new ExecutorCompletionService<LockState>(exec);

            final int numberOfTasks = 1000;
            IntStream.range(0, numberOfTasks)
                    .map(x -> new Random().nextInt(2))
                    .mapToObj(x -> (x == 0) ? writerTask : readerTask)
                    .forEach(service::submit);

            int errorCount = 0;
            for (int i = 0; i < numberOfTasks; ++i) {
                var state = service.take().get();
                if (state.error) {
                    errorCount++;
                    logger.error("Lock state error: Readers={}, Writer={}", state.readerCount, state.writerCount);
                }
            }

            Assertions.assertEquals(0, errorCount);
        }
    }

    @Nested
    @DisplayName("Write rentrancy tests")
    class WriteReentrancyTests {
        /**
         * Tests that a thread can acquire the write lock multiple times
         * and requires the same number of unlocks to release the lock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock and unlock - WWW")
        public void reentrantWriteWrite() {
            var numLocks = 3;
            for (int i = 0; i < numLocks; i++)
                rwLock.lockWrite();

            for (int i = 0; i < numLocks; i++)
                rwLock.unlock();

            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

        /**
         * Tests that a thread can acquire the write lock multiple times
         * and doesn't release the lock until the last unlock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock maintains exclusivity - WW ")
        public void reentrantWriteWriteExclusive() {
            // lock the write lock on the current thread
            rwLock.lockWrite();

            // Create a request for a read lock on a different thread
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);
            executor.submit(group.createReaderTask());
            group.awaitReadyToLock();
            group.lockInOrder();

            // Ensure that this read is waiting
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // lock the write lock on the current thread again
            rwLock.lockWrite();

            // Test that this read is still waiting
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the write lock on the current thread
            rwLock.unlock();

            // Test that this read is still waiting
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the write lock on the current thread again
            rwLock.unlock();

            // Test that this read is now locked
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
        }

        /**
         * Tests that a thread can acquire the read lock after acquiring the write lock
         * and requires the same number of unlocks to release the lock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock - WRR")
        public void reentrantWriteRead() {
            rwLock.lockWrite();
            rwLock.lockRead();
            rwLock.lockRead();

            for (int i = 0; i < 3; i++)
                rwLock.unlock();

            // Ensure that the lock is released
            var numLocks = 3;
            for (int i = 0; i < numLocks; i++)
                rwLock.lockWrite();

            for (int i = 0; i < numLocks; i++)
                rwLock.unlock();

            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

        /**
         * Tests that a thread can acquire the read lock after acquiring the write lock
         * and requires the same number of unlocks to release the lock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock - WRW")
        public void reentrantWriteReadWrite() {
            rwLock.lockWrite();
            rwLock.lockRead();
            rwLock.lockWrite();

            for (int i = 0; i < 3; i++)
                rwLock.unlock();

            // Ensure that the lock is released
            var numLocks = 3;
            for (int i = 0; i < numLocks; i++)
                rwLock.lockWrite();

            for (int i = 0; i < numLocks; i++)
                rwLock.unlock();

            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

        /**
         * Tests that excusivity of a WRR reentrant lock is maintained
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock maintains exclusivity - WRR ")
        public void reentrantWriteReadExclusive() {
            // lock on the main thread
            rwLock.lockWrite();
            rwLock.lockRead();
            rwLock.lockRead();

            // Create a request for a read lock on a different thread
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);
            executor.submit(group.createReaderTask());
            group.awaitReadyToLock();
            group.lockInOrder();

            // Ensure that this read is waiting
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the second read lock on the current thread
            rwLock.unlock();

            // Test that this read is still waiting
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the first read lock on the current thread
            rwLock.unlock();

            // Test that this read is still waiting
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the write lock on the current thread again
            rwLock.unlock();

            // Test that this read is now locked
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
        }

    }

    @Nested
    @DisplayName("Read rentrancy tests")
    class ReadReentrancyTests {
        /**
         * Tests that a thread can acquire the read lock multiple times
         * and requires the same number of unlocks to release the lock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock and unlock - RRR")
        public void reentrantReadRead() {
            var numLocks = 3;
            for (int i = 0; i < numLocks; i++)
                rwLock.lockRead();

            for (int i = 0; i < numLocks; i++)
                rwLock.unlock();

            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.unlock());
        }

        /**
         * Tests that a thread can acquire the read lock multiple times
         * and doesn't release the lock until the last unlock
         */

        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock maintains the lock on unlock - RR ")
        public void reentrantReadReadUnlock() {
            rwLock.lockRead();

            // Create a request for a read lock on a different thread
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);
            executor.submit(group.createWriterTask());
            group.awaitReadyToLock();
            group.lockInOrder();

            // Ensure that this read is waiting
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // lock the read lock on the current thread again
            rwLock.lockRead();

            // Test that this read is still waiting
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the write lock on the current thread
            rwLock.unlock();

            // Test that this read is still waiting
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.WAITING, group.getLockStatus(0));

            // unlock the write lock on the current thread again
            rwLock.unlock();

            // Test that this read is now locked
            TestUtils.SleepRandomTime(STD_WAIT_TIME);
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
        }

        /**
         * Tests that a thread can acquire the read lock multiple times
         * and doesn't release the lock until the last unlock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant read lock allows other reads - RRR ")
        public void reentrantReadReadAllowsRead() {
            rwLock.lockRead();
            rwLock.lockRead();

            // Create a request for a read lock on a different thread
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);
            executor.submit(group.createReaderTask());
            group.awaitReadyToLock();
            group.lockInOrder();

            // Ensure that this read is waiting
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));
        }

        /**
         * Tests that a thread can acquire the read lock 
         * while another thread has multiple read locks
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests can gain reentrant read lock while there are other reads")
        public void reentrantReadAllowsReadRead() {
            rwLock.lockRead();

            // Create a request for a read lock on a different thread
            var group = new ExecutionGroup(rwLock, STD_WAIT_TIME);
            executor.submit(group.createReaderTask());
            group.awaitReadyToLock();
            group.lockInOrder();

            // Ensure that this read is waiting
            Assertions.assertEquals(ControlledExecution.LockStatus.LOCKED, group.getLockStatus(0));

            rwLock.lockRead();
        }

        /**
         * Tests that a thread cannot acquire the write lock after acquiring the read
         * lock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock - RW")
        public void reentrantReadWriteThrowsException() {
            rwLock.lockRead();
            
            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.lockWrite());
        }

        /**
         * Tests that a thread cannot acquire the write lock after acquiring the read
         * lock
         */
        @Test
        @Timeout(STD_WAIT_TIME * 10)
        @DisplayName("Tests reentrant lock - RRW")
        public void reentrantReadReadWriteThrowsException() {
            rwLock.lockRead();
            rwLock.lockRead();
            
            Assertions.assertThrows(IllegalMonitorStateException.class, () -> rwLock.lockWrite());
        }
    }

}