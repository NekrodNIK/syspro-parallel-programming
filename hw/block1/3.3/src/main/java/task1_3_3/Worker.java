package task1_3_3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;

public class Worker {
  private ThreadFactory threadFactory;
  private BlockingQueue<Task<?>> taskQueue;
  
  public Worker(ThreadFactory threadFactory, BlockingQueue<Task<?>> taskQueue) {
    this.threadFactory = threadFactory;
    this.taskQueue = taskQueue;
    this.respawn();
  }

  private void respawn() {
    var t = threadFactory.newThread(() -> {
      try {
        loop();
      } finally {
        respawn();
      }
    });
    t.start();
  }

  private void loop() {
    for (;;) {
      Task<?> task;
      try {
        task = taskQueue.take();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
      doTask(task);
    }
  }

  private <V> void doTask(Task<V> task) {
    V value;
    try {
      value = task.callback.call();
    } catch (Exception e) {
      task.future.setThrowable(e);
      return;
    } catch (Throwable t) {
      task.future.setThrowable(t);
      throw t;
    }
    task.future.setValue(value);
  }
}
