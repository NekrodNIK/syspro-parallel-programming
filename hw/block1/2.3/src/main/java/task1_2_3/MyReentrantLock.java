package task1_2_3;

public class MyReentrantLock {
  private final int START_DELAY = 1;
  private final int MAX_DELAY = 1000;

  private final NonReentrantLock lock;
  private Thread owner;
  private int holdCount;

  public MyReentrantLock(NonReentrantLockFactory factory) {
    lock = factory.create();
    owner = null;
    holdCount = 0;
  }

  /**
   * Acquire the lock.
   * if the lock is already acquired by another thread, the thread is blocked,
   * otherwise, nothing happens. 
   */
  public void lock() {
    int delay = START_DELAY;

    while (!tryLock()) {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      delay = Math.min(delay * 2, MAX_DELAY);
    }
  }

  /**
   * Try to acquire the lock.
   * 
   * @return status of acquisition
   */
  public boolean tryLock() {
    Thread current = Thread.currentThread();
    boolean success = false;

    lock.lock();
    try {
      if (owner == null) {
        owner = current;
      }
      if (owner == current) {
        holdCount++;
        success = true;
      }
    } finally {
      lock.unlock();
    }

    return success;
  }

  /**
   * Release the lock.
   * 
   * @throws IllegalMonitorStateException if the method called by non-owning
   *                                      thread
   */
  public void unlock() {
    Thread current = Thread.currentThread();

    lock.lock();
    try {
      if (holdCount == 0 || owner != current)
        throw new IllegalMonitorStateException("MyReentrantLock#lock() was called by a non-owning thread");
      holdCount--;
      if (holdCount == 0) {
        owner = null;
      }
    } finally {
      lock.unlock();
    }
  }
}
