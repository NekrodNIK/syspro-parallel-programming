package task1_3_1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;

public class JoinFuture<V> {
  private final Thread thread;
  private Exception exception = null;
  private V result = null;

  JoinFuture(ThreadFactory factory, Callable<V> task) {
    thread = factory.newThread(() -> {
      try {
        result = task.call();
      } catch (Exception e) {
        exception = e;
      }
    });
    thread.start();
  }

  /**
   * Waits if necessary for the computation to complete, and then retrieves its
   * result.
   * 
   * @returns the computed result.
   * @throws ExecutionException - if the computation threw an exception.
   * 
   */
  public V get() throws ExecutionException {
    for (;;) {
      try {
        thread.join();
        break;
      } catch (InterruptedException e) {
        thread.interrupt();
      }
    }

    if (exception != null) {
      throw new ExecutionException(exception);
    }
    return result;
  }

  /**
   * Returns `true` if this task completed. Completion may be due to normal
   * termination or
   * an exception -- in all of these cases, this method will return true.
   */
  public boolean isDone() {
    return !thread.isAlive();
  }
}
