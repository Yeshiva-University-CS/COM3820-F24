package edu.yu.parallel.execution;

import edu.yu.parallel.RWLockInterface;

import java.util.concurrent.Phaser;

public class ExecutionGroup extends AbstractExecutionGroup<Void> {

    private volatile boolean readyToLock;
    private final Phaser entryPhaser = new Phaser(1); // register the execution group

    public ExecutionGroup(RWLockInterface rwLock, long defaultWaitTime) {
        super(rwLock, defaultWaitTime);
    }

    protected ExecutionController newExecutionController() {
        return new CoordinatedController(getRWLock(), entryPhaser);
    }

    public void awaitReadyToLock() {
        entryPhaser.arriveAndAwaitAdvance();
        readyToLock = true;
    }

    protected boolean threadsAreReadyToLock() {
        return readyToLock;
    }

}
