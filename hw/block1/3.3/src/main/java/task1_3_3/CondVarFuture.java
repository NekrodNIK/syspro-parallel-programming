package task1_3_3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CondVarFuture<V> {
  private final Lock lock;
  private final Condition cond;

  private boolean done = false;
  private V value = null;
  private Throwable throwable = null;

  public CondVarFuture() {
    this.lock = new ReentrantLock();
    this.cond = lock.newCondition();
  }

  /**
   * Waits if necessary for the computation to complete, and then retrieves its
   * result.
   * 
   * @returns the computed result
   * @throws ExecutionException - if the computation threw an exception
   */
  public V get() throws ExecutionException {
    boolean interrupted = false;
    
    lock.lock();
    try {
      while (!done) {
        try {
          cond.await();
        } catch (InterruptedException t) {
          interrupted = true;
        }
      }
    } finally {
      lock.unlock();
    }

    if (interrupted) {
      Thread.currentThread().interrupt();
    }

    if (throwable != null) {
      throw new ExecutionException(throwable);
    }

    return value;
  }

  /**
   * Sets the value and notifies the thread waiting in the {@link #get()} method
   * that
   * this {@link CondVarFuture} has done execution.
   */
  public void setValue(V v) {
    lock.lock();
    try {
      if (done) {
        return;
      }
      value = v;
      done = true;
      cond.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Sets the throwable and notifies the thread waiting in the {@link #get()}
   * method that
   * this {@link CondVarFuture} has done execution.
   */
  public void setThrowable(Throwable t) {
    lock.lock();
    try {
      if (done) {
        return;
      }
      throwable = t;
      done = true;
      cond.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * @returns `true` if this task completed. Completion may be due to normal
   *          termination or
   *          an exception -- in all of these cases, this method will return true.
   */
  public boolean isDone() {
    lock.lock();
    try {
      return done;
    } finally {
      lock.unlock();
    }
  }
}
