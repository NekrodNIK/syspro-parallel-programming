package task1_3_3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

public class SingleThreadExecutorService {
  private final BlockingQueue<Task<?>> queue;
  private final Worker worker;

  public SingleThreadExecutorService(ThreadFactory factory) {
    queue = new LinkedBlockingQueue<>();
    worker = new Worker(factory, queue);
  }

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
