package task1_3_3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSingleThreadExecutorService {
  private class MockThreadFactory implements ThreadFactory {
    public long threadsNumber = 0;

    @Override
    public Thread newThread(Runnable r) {
      threadsNumber++;
      return new Thread(r);
    }
  }

  private final MockThreadFactory factory = new MockThreadFactory();
  private SingleThreadExecutorService service;

  @BeforeEach
  void setUp() {
    service = new SingleThreadExecutorService(factory);
  }

  @Test
  void testSimple() throws ExecutionException {
    var f1 = service.submit(() -> "DON'T");
    var f2 = service.submit(() -> "PANIC!");
    var f3 = service.submit(() -> f1.get() + " " + f2.get());
    assertEquals("DON'T PANIC!", f3.get());
    assertEquals(1, factory.threadsNumber);
  }

  @Test
  void testLoop() throws ExecutionException {
    int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    List<CondVarFuture<Void>> futures = new ArrayList<CondVarFuture<Void>>(Collections.nCopies(10, null));
    for (int i = 0; i < 10; i++) {
      var j = i;
      Callable<Void> callable = () -> {
        arr[j] *= 2;
        return null;
      };
      futures.set(j, service.submit(callable));
    }

    int expected[] = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 };
    for (int i = 0; i < 10; i++) {
      futures.get(i).get();
      assertEquals(expected[i], arr[i]);
    }
  }

  @Test
  void testSequential() throws ExecutionException {
    List<CondVarFuture<Integer>> future = new ArrayList<CondVarFuture<Integer>>();
    future.add(null);
    for (int i = 0; i < 10; i++) {
      CondVarFuture<Integer> next = service.submit(() -> {
        if (future.get(0) != null) {
          return future.get(0).get() * 2;
        } else {
          return 2;
        }
      });
      next.get();
      future.set(0, next);
    }
    assertEquals(1024, future.get(0).get());
  }

  @Test
  void testSequantialFibonacci() throws ExecutionException {
    List<CondVarFuture<Integer>> futures = new ArrayList<CondVarFuture<Integer>>();
    futures.add(service.submit(() -> 0));
    futures.add(service.submit(() -> 1));

    final int N = 10;
    for (int i = 0; i < N-1; i++) {
      CondVarFuture<Integer> new_future = service.submit(() -> futures.get(0).get() + futures.get(1).get());
      new_future.get();
      futures.set(0, futures.get(1));
      futures.set(1, new_future);
    }
    
    assertEquals(55, futures.get(1).get());
  }

  @Test
  void testException() {
    Callable<Void> runnable = () -> {
      throw new RuntimeException("So Long, and Thanks for All the Fish");
    };
    var f1 = service.submit(runnable);
    assertThrows(ExecutionException.class, () -> f1.get());
    var f2 = service.submit(runnable);
    try {
      f2.get();
    } catch (ExecutionException e) {
      assertInstanceOf(RuntimeException.class, e.getCause());
    }
  }
}
