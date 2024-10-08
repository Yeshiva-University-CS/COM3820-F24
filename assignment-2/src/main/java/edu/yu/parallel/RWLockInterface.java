package edu.yu.parallel;

/* Defines a "read/write lock" interface.  It allows multiple threads to lock
 * in read mode concurrently, but only one thread is allowed to lock in write
 * mode concurrently.
 *
 * Motivation: multiple threads can read from a shared resource without causing
 * concurrency errors.  Concurrency errors only occur when either reads and
 * writes or if multiple writes take place concurrently.
 *
 * The locking rules are as follows:  
 * 
 *     A thread invoking lockRead() is granted the lock if no other thread 
 *     currently holds a write lock and no pending write requests exist. 
 * 
 *     Once a thread requests a write lock, no new threads may acquire the read 
 *     lock until the write request is satisfied. However, existing readers may 
 *     continue their operations until they release their read locks.
 * 
 *     Blocking threads are queued in the order that they requested the lock, 
 *     with separate queues for readers and writers. 
 * 
 *     Writers have priority over new readers once a write request is pending. 
 *     Readers that acquired the lock before a write request was made are allowed 
 *     to complete their reads, but no new readers are allowed until the pending 
 *     write request is satisfied.
 *
 * You must handle reentrancy in the following manner:
 * 
 *    A thread holding a read lock may acquire additional read locks. Each 
 *    call to unlock() releases one read lock, and the thread must release all 
 *    acquired read locks for full relinquishment. 
 * 
 *    A thread holding a read lock cannot acquire a write lock. If it tries to do
 *    so, throw an IllegalMonitorStateException.
 * 
 *    A thread holding a write lock may acquire additional read or write locks. 
 *    If a thread holding a write lock acquires a read lock, it retains exclusive 
 *    access to the resource, maintaining the semantics of the write lock. This 
 *    means that no other thread (neither readers nor writers) may access the 
 *    resource until the thread has fully released all locks. 
 * 
 *    Each call to unlock() releases one level of locking, either for the read 
 *    or write lock held by the thread. The thread must call unlock() for each 
 *    acquired lock to fully relinquish access to the resource.
 * 
 * 
 */

public interface RWLockInterface {

    /**
     * Acquires the lock iff:
     * 1) No other thread is writing, AND
     * 2) No other threads have requested write access
     * Otherwise, the invoking thread is blocked until the writing thread AND
     * previously blocked threads requesting write access have released the lock
     * <p>
     * NOTE: Readers that request the lock before a subsequent writer will be 
     * granted the lock before the writer. That is, if a reader is currently blocked
     * waiting for a writer to release the lock, and a new writer requests the lock,
     * the new writer will be blocked until the reader obtains the lock and then 
     * relinquishes it. If subsequently a new reader requests the lock, it will be
     * be queued behind the second writer.
     */
    void lockRead();

    /**
     * Acquires the lock iff no other thread currently has acquired the lock in
     * either read or write mode.  Otherwise, the invoking thread is blocked until
     * all previous threads have released the lock.
     * <p>
     * NOTE: blocking threads are queued in the order that they requested the lock
     */
    void lockWrite();

    /**
     * Each call to unlock() releases one level of locking for the invoking thread. 
     * If the thread holds multiple locks due to reentrancy, each unlock() call 
     * releases one lock. The thread must release all locks it has acquired 
     * (whether read or write) before the lock is fully relinquished. 
     *
     * @throws IllegalMonitorStateException if invoking thread doesn't currently own
     *                                      the lock.
     */
    void unlock();

} // interface