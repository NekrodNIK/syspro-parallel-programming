package task1_3_1;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

public class ThreadPerTaskExecutorService {
  final private ThreadFactory factory;

  public ThreadPerTaskExecutorService(ThreadFactory factory) {
    this.factory = factory;
  }

  /**
   * Submits a value-returning task for execution and returns a `JoinFuture`
   * representing the pending results of the task.
   * The `JoinFuture`s `get` method will return the task's result upon successful
   * completion.
   */
  <T> JoinFuture<T> submit(Callable<T> task) {
    return new JoinFuture<T>(factory, task);
  }
}
