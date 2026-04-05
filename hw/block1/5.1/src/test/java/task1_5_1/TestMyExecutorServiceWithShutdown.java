package task1_5_1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MyExecutorServiceWithShutdownTest {
  static class MockExecutor implements MyExecutorService {
    private final ExecutorService executor = Executors.newFixedThreadPool(6);

    @Override
    public <T> MyFuture<T> submit(Callable<T> task) {
      Future<T> future = executor.submit(task);
      return new MyFuture<T>() {
        @Override
        public T get() throws ExecutionException {
          try {
            return future.get();
          } catch (Throwable t) {
            throw new ExecutionException(t);
          }
        }

        @Override
        public boolean isDone() {
          return future.isDone();
        }
      };
    }
  }

  MyExecutorService inner;
  MyExecutorServiceWithShutdown executor;

  @BeforeEach
  void setUp() {
    inner = new MockExecutor();
    executor = new MyExecutorServiceWithShutdown(inner);
  }

  @Test
  void testSubmitAfterShutdownThrowsException() {
    executor.shutdown();
    assertThrows(IllegalArgumentException.class, () -> {
      executor.submit(() -> null);
    });
  }

  @Test
  void testIsShutdown() {
    assertFalse(executor.isShutdown());
    executor.shutdown();
    assertTrue(executor.isShutdown());
  }

  @Test
  void testIsTerminatedAfterShutdown() throws Exception {
    var startLatch = new CountDownLatch(1);
    
    executor.submit(() -> {
      startLatch.await();
      Thread.sleep(1000);
      return null;
    });

    executor.shutdown();
    assertFalse(executor.isTerminated());
    
    startLatch.countDown();
    executor.awaitTermination();
    assertTrue(executor.isTerminated());
  }

  @Test
  void testAwaitTerminationBlocks() throws Exception {
    var startLatch = new CountDownLatch(1);
    
    executor.submit(() -> {
      startLatch.await();
      Thread.sleep(200);
      return null;
    });

    executor.shutdown();

    long start = System.currentTimeMillis();
    startLatch.countDown();
    executor.awaitTermination();
    long duration = System.currentTimeMillis() - start;
    
    assertTrue(duration >= 200);
    assertTrue(executor.isTerminated());
  }

  @Test
  void testMultipleShutdownCallsDoNothing() {
    executor.shutdown();
    executor.shutdown();
    executor.shutdownNow();
    assertTrue(executor.isShutdown());
  }

  @Test
  void testCallableWithException() throws InterruptedException {
    var latch = new CountDownLatch(1);
    
    Callable<Integer> callable = () -> {
      latch.countDown();
      throw new RuntimeException();
    };

    executor.submit(callable);
    executor.shutdown();

    latch.await();
    Thread.sleep(1000);
    assertTrue(executor.isTerminated());
  }

  @Test
  void testShutdownNow() throws InterruptedException {
    var startLatch = new CountDownLatch(10);
    
    for (int i = 0; i < 100; i++) {
      executor.submit(() -> {
        startLatch.countDown();
        while (true) {}
      });
    }

    startLatch.await();
    List<Callable<?>> discarded = executor.shutdownNow();
    assertTrue(discarded.size() <= 90);
  }

  @Test
  void testMultipleThreadsAwaitTermination() throws InterruptedException {
    var startLatch = new CountDownLatch(3);
    
    for (int i = 0; i < 10; i++) {
      executor.submit(() -> {
        startLatch.await();
        return null;
      });
    }
    executor.shutdown();

    var t1 = new Thread(() -> {
      startLatch.countDown();
      executor.awaitTermination();
    });
    var t2 = new Thread(() -> {
      startLatch.countDown();
      executor.awaitTermination();
    });
    var t3 = new Thread(() -> {
      startLatch.countDown();
      executor.awaitTermination();
    });
    t1.join();
    t2.join();
    t3.join();
  }
}
