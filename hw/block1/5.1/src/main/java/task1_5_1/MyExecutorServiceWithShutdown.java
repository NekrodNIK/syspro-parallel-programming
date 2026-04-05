package task1_5_1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class MyExecutorServiceWithShutdown implements MyExecutorService {
  /**
   * states of MyExecutorServiceWithShutdown
   * -- Transitions --
   * AcceptingTasks -> Shutdown
   * AcceptingTasks -> ForceShutdown
   * Shutdown -> Terminated
   * ForceShutdown -> Terminated
   */
  private enum State {
    // default service state, the service accepts tasks.
    AcceptingTasks,
    // no longer accepts new tasks and executes all previously submitted tasks.
    Shutdown,
    // no longer accepts new tasks,
    // continues to execute those already running,
    // discards pending tasks.
    ForceShutdown,
    // No longer accepts new tasks and will no longer execute any task.
    Terminated,
  }

  private class Task<V> {
    Callable<V> callback;

    Task(Callable<V> callback) {
      this.callback = callback;
    }
  }

  private final MyExecutorService inner;
  private State state;
  private final Set<Task<?>> pending;
  private int executing;

  public MyExecutorServiceWithShutdown(MyExecutorService inner) {
    this.inner = inner;
    this.state = State.AcceptingTasks;
    this.pending = new HashSet<>();
    this.executing = 0;
  }

  /**
   * Forwarder to inner.submit.
   * 
   * Throws `IllegalArgumentException` if user tries to submit task after
   * `shutdown`.
   */
  synchronized public <V> MyFuture<V> submit(Callable<V> callback) {
    if (state != State.AcceptingTasks)
      throw new IllegalArgumentException();

    var task = new Task<V>(null);
    task.callback = () -> {
      synchronized (this) {
        if (state == State.ForceShutdown) {
          return null;
        }
        pending.remove(task);
        executing++;
      }

      Exception exception = null;
      V result = null;

      try {
        result = callback.call();
      } catch (Exception e) {
        exception = e;
      }

      synchronized (this) {
        executing--;
        if (isShutdown() && executing == 0 && pending.size() == 0) {
          state = State.Terminated;
          this.notifyAll();
        }
      }

      if (exception == null) {
        return result;
      } else {
        throw exception;
      }
    };

    pending.add(task);
    return inner.submit(task.callback);
  }

  /**
   * Initiates an orderly shutdown in which previously submitted tasks are
   * executed, but no new tasks will be accepted.
   * Invocation has no additional effect if already shut down.
   * 
   * This method does not wait for previously submitted tasks to complete
   * execution. Use `awaitTermination` to do that.
   *
   */
  synchronized public void shutdown() {
    if (!isShutdown()) {
      state = State.Shutdown;
    }
  }

  /**
   * Returns true if this executor has been shut down.
   * 
   * True does not mean all submitted tasks has been completed. Use `isTerminated`
   * to check that.
   *
   */
  synchronized public boolean isShutdown() {
    return state == State.Shutdown || state == State.ForceShutdown || state == State.Terminated;
  }

  /**
   * Returns true if all tasks have completed following shut down.
   * Note that isTerminated is never true unless either shutdown or shutdownNow
   * was called first.
   * 
   */
  synchronized public boolean isTerminated() {
    return state == State.Terminated;
  }

  /**
   * Forbids submission of new tasks (equivalent to `shutdown`), halts the
   * processing of waiting tasks and
   * returns a list of the tasks that were awaiting execution.
   * 
   * This method does not wait for actively executing tasks to terminate. Any
   * already executing task **will not** be returned
   * by this method. Use `awaitTermination` to ensure all tasks are finished.
   * 
   */
  synchronized public List<Callable<?>> shutdownNow() {
    List<Callable<?>> result = new ArrayList<>();
    if (state != State.ForceShutdown && state != State.Terminated) {
      state = State.ForceShutdown;
      for (Task<?> task : pending) {
        result.add(task.callback);
      }
      pending.clear();
    }
    return result;
  }

  /**
   * Blocks until all tasks have completed execution after a shutdown request.
   * 
   */
  synchronized public boolean awaitTermination() {
    boolean interrupted = false;
    while (state != State.Terminated) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        interrupted = true;
      }
    }
    if (interrupted) {
      Thread.currentThread().interrupt();
    }
    return true;
  }
}
