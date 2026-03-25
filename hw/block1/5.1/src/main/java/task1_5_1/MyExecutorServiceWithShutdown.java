package task1_5_1;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MyExecutorServiceWithShutdown implements MyExecutorService {
  private enum State {
    AcceptingTasks,
    Shutdown,
    ForceShutdown,
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
      var result = callback.call();
      synchronized (this) {
        executing--;
        if (isShutdown() && executing == 0 && pending.size() == 0) {
          state = State.Terminated;
          this.notifyAll();
        }
      }
      return result;
    };

    pending.add(task);
    return inner.submit(task.callback);
  }

  synchronized public void shutdown() {
    if (!isShutdown()) {
      state = State.Shutdown;
    }
  }

  synchronized public boolean isShutdown() {
    return state == State.Shutdown || state == State.ForceShutdown || state == State.Terminated;
  }

  synchronized public boolean isTerminated() {
    return state == State.Terminated;
  }

  synchronized public List<Callable<?>> shutdownNow() {
    if (!isShutdown()) {
      state = State.ForceShutdown;
      List<Callable<?>> result = pending.stream().map((item) -> item.callback).collect(Collectors.toList());
      pending.clear();
      return result;
    } else {
      return new LinkedList<>();
    }
  }

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
