package task1_3_3;

import java.util.concurrent.Callable;

public class Task<V> {
  public final Callable<V> callback;
  public final CondVarFuture<V> future;

  Task(Callable<V> callback, CondVarFuture<V> future) {
    this.callback = callback;
    this.future = future;
  };
}
