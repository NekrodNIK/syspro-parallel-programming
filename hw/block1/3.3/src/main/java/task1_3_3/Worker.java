package task1_3_3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;

public class Worker {
  private ThreadFactory threadFactory;
  private BlockingQueue<Task<?>> taskQueue;

  /** Creates worker and starts first thread. */
  public Worker(ThreadFactory threadFactory, BlockingQueue<Task<?>> taskQueue) {
    this.threadFactory = threadFactory;
    this.taskQueue = taskQueue;
    this.respawn();
  }

  /**
   * Spawns a new worker thread.
   * 
   * The new thread runs the {@link #loop()} method.
   * After {@link #loop()} exits (for any reason), {@link #respawn()} is called
   * again to ensure a replacement thread is created.
   */
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

  /**
   * Worker loop.
   * 
   * Takes tasks from the queue and executes them via
   * {@link #doTask(Task)}.
   * If thread interrupted while waiting for a task, method returns.
   */
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

  /**
   * Executes a single task.
   * 
   * On success: sets value via future.
   * On checked exception: sets future exceptionally, worker continues.
   * On unchecked throwable: sets future exceptionally, rethrows to kill thread.
   */
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
