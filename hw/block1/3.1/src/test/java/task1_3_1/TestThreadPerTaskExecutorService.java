package task1_3_1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestThreadPerTaskExecutorService {
  private class MockThreadFactory implements ThreadFactory {
    public long threadsNumber = 0;

    @Override
    public Thread newThread(Runnable r) {
      threadsNumber++;
      return new Thread(r);
    }
  }

  private final MockThreadFactory factory = new MockThreadFactory();
  private ThreadPerTaskExecutorService service;

  @BeforeEach
  void setUp() {
    service = new ThreadPerTaskExecutorService(factory);
  }

  @Test
  void testSimple() throws ExecutionException {
    var f1 = service.submit(() -> "DON'T");
    var f2 = service.submit(() -> "PANIC!");
    var f3 = service.submit(() -> f1.get() + " " + f2.get());
    assertEquals("DON'T PANIC!", f3.get());
    assertEquals(3, factory.threadsNumber);
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
