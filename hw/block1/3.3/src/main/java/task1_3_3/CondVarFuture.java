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

  public V get() throws ExecutionException {
    lock.lock();
    try {
      while (!done) {
        try {
          cond.await();
        } catch (InterruptedException t) {
          Thread.currentThread().interrupt();
        }
      }
    } finally {
      lock.unlock();
    }

    if (throwable != null) {
      throw new ExecutionException(throwable);
    }

    return value;
  }

  public void setValue(V v) {
    lock.lock();
    try {
      if (done) {
        return;
      }
      value = v;
      done = true;
      cond.signal();
    } finally {
      lock.unlock();
    }
  }

  public void setThrowable(Throwable t) {
    lock.lock();
    try {
      if (done) {
        return;
      }
      throwable = t;
      done = true;
      cond.signal();
    } finally {
      lock.unlock();
    }
  }

  public boolean isDone() {
    lock.lock();
    try {
      return done;
    } finally {
      lock.unlock();
    }
  }
}
