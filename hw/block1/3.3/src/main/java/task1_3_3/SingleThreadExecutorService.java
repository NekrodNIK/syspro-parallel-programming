package task1_3_3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * Creates an ExecutorService that uses a single worker thread operating off an
 * unbounded queue.
 * Note however that if this single thread terminates due to a failure during
 * execution, a new one will take its place
 * if needed to execute subsequent tasks.
 * 
 * Tasks are guaranteed to execute sequentially, and no more than one task will
 * be active at any given time.
 */
public class SingleThreadExecutorService {
  private final BlockingQueue<Task<?>> queue;
  private final Worker worker;

  public SingleThreadExecutorService(ThreadFactory factory) {
    queue = new LinkedBlockingQueue<>();
    worker = new Worker(factory, queue);
  }

  /**
   * Submits a value-returning task for execution and returns a `CondVarFuture`
   * representing the pending results of the task.
   * The `CondVarFuture`s `get` method will return the task's result upon
   * successful completion.
   */
  public <V> CondVarFuture<V> submit(Callable<V> callback) {
    var future = new CondVarFuture<V>();
    var task = new Task<V>(callback, future);

    try {
      queue.put(task);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    return future;
  }
}
