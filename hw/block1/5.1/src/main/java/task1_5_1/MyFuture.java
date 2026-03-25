package task1_5_1;

import java.util.concurrent.ExecutionException;

interface MyFuture<V> {
  public V get() throws ExecutionException;

  public boolean isDone();
}
