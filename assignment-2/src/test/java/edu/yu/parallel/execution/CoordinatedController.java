package edu.yu.parallel.execution;

import edu.yu.parallel.RWLockInterface;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;

public class CoordinatedController implements ExecutionController {
    private final RWLockInterface lock;
    private final Phaser entryPhaser;
    private final CountDownLatch lockLatch = new CountDownLatch(1);
    private final CountDownLatch executionLatch = new CountDownLatch(1);
    public CoordinatedController(RWLockInterface lock, Phaser entryPhaser) {
        this.lock = lock;
        this.entryPhaser = entryPhaser;
        entryPhaser.register();
    }

    @Override
    public void awaitForAllThreadsToHaveStarted() {
        entryPhaser.arriveAndAwaitAdvance();
    }

    @Override
    public void awaitPermissionToLock() {
        try {
            lockLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void awaitPermissionToCompleteExecution() {
        try {
            executionLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void permitLockRequest() {
        lockLatch.countDown();
    }

    @Override
    public void completeExecution() {
        executionLatch.countDown();
    }

    @Override
    public void lockForRead() {
        lock.lockRead();
    }

    @Override
    public void lockForWrite() {
        lock.lockWrite();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
