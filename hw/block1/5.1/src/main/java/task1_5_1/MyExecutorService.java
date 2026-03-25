package task1_5_1;

import java.util.concurrent.Callable;

interface MyExecutorService {
  <T> MyFuture<T> submit(Callable<T> task);
}
