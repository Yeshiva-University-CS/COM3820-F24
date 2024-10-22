package edu.yu.parallel.execution;

import edu.yu.parallel.RWLockInterface;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This class is **NOT** thread safe. It must only be used from a single thread.
 *
 * @param <T>
 */
abstract class AbstractExecutionGroup<T> {

    private final RWLockInterface rwLock;
    private final long defaultWaitTime;
    private final ArrayList<ControlledExecution<T>> executionTasks = new ArrayList<>();

    protected AbstractExecutionGroup(RWLockInterface rwLock, long defaultWaitTime) {
        this.defaultWaitTime = defaultWaitTime;
        this.rwLock = rwLock;
    }

    protected RWLockInterface getRWLock() {
        return rwLock;
    }

    public Callable<T> createReaderTask(Callable<T> callable) {
        if (threadsAreReadyToLock())
            throw new IllegalStateException("Cannot create more tasks after awaitAllThreadsStarted is called");
        var controller = this.newExecutionController();
        var task = new Reader<>(executionTasks.size(), controller, callable);
        executionTasks.add(task);
        return task;
    }

    public Callable<T> createReaderTask() {
        return createReaderTask(() -> null);
    }

    public Callable<T> createWriterTask(Callable<T> callable) {
        if (threadsAreReadyToLock())
            throw new IllegalStateException("Cannot create more tasks after awaitAllThreadsStarted is called");
        var controller = this.newExecutionController();
        var task = new Writer<>(executionTasks.size(), controller, callable);
        executionTasks.add(task);
        return task;
    }

    public Callable<T> createWriterTask() {
        return createWriterTask(() -> null);
    }

    public void lockInOrder() {
        lockInOrder(this.defaultWaitTime);
    }

    public void lockInOrder(long sleepWaitTime) {
        if (!threadsAreReadyToLock())
            throw new IllegalStateException("awaitAllThreadsStarted must be called first");

        for (ControlledExecution<T> executionTask : executionTasks) {
            executionTask.permitLocking();
            do {
                try {
                    Thread.sleep(sleepWaitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (executionTask.getLockStatus() == ControlledExecution.LockStatus.READY);
        }
    }

    public ControlledExecution.LockStatus getLockStatus(int seqNum) {
        return executionTasks.get(seqNum).getLockStatus();
    }

    public void completeExecution(int seqNum) {
        completeExecution(seqNum, this.defaultWaitTime);
    }

    public void completeExecution(int seqNum, long sleepWaitTime) {
        executionTasks.get(seqNum).completeExecution();
        try {
            Thread.sleep(sleepWaitTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Blocks until all task threads have started and are ready to lock
     */
    abstract public void awaitReadyToLock();

    /**
     * @return true if awaitReadyToLock has been called and threads are ready to lock, otherwise will return false
     */
    abstract protected boolean threadsAreReadyToLock();

    /**
     * Create a new instance of an IExecutionController to be passed into
     * the constructor of the Reader or Writer
     *
     * @return a new instance of an IExecutionController
     */
    abstract protected ExecutionController newExecutionController();

    /**
     * A ControlledExecution Writer that requires the writer lock
     *
     * @param <U>
     */
    private static class Writer<U> extends ControlledExecution<U> {
        private Writer(int seqNum, ExecutionController controller, Callable<U> callable) {
            super("W" + seqNum, seqNum, controller, callable);
        }

        @Override
        protected void lock() {
            this.lockForWrite();
        }
    }

    /**
     * A ControlledExecution Reader that requires the reader lock
     *
     * @param <U>
     */
    private static class Reader<U> extends ControlledExecution<U> {
        private Reader(int seqNum, ExecutionController controller, Callable<U> callable) {
            super("R" + seqNum, seqNum, controller, callable);
        }

        @Override
        protected void lock() {
            this.lockForRead();
        }
    }

}
