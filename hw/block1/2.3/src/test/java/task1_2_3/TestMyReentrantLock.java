package task1_2_3;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMyReentrantLock {
  private class ImplNonReentrantLock implements NonReentrantLock {
    private final ReentrantLock lock;

    ImplNonReentrantLock() {
      lock = new ReentrantLock();
    }

    @Override
    public void lock() {
      if (lock.isHeldByCurrentThread()) {
        throw new IllegalMonitorStateException();
      }
      lock.lock();
    }

    @Override
    public void unlock() {
      lock.unlock();
    }
  }

  private class ImplNonReentrantLockFactory implements NonReentrantLockFactory {
    @Override
    public NonReentrantLock create() {
      return new ImplNonReentrantLock();
    }
  }

  private final NonReentrantLockFactory factory = new ImplNonReentrantLockFactory();
  private MyReentrantLock myLock;

  @BeforeEach
  void setUp() {
    myLock = new MyReentrantLock(factory);
  }

  @Test
  void testSimple() {
    myLock.lock();
    myLock.unlock();
  }

  @Test
  void testLoop() throws InterruptedException {
    Runnable func = () -> {
      final int MAX = 10000;
      for (int i = 0; i < MAX; i++)
        myLock.lock();
      for (int i = 0; i < MAX; i++)
        myLock.unlock();
    };
    Thread t1 = new Thread(func);
    Thread t2 = new Thread(func);
    t1.start();
    t2.start();
    t1.join();
    t2.join();
  }

  @Test
  void testUnlockReleasedLock() {
    myLock.lock();
    myLock.lock();
    myLock.unlock();
    myLock.unlock();
    assertThrows(IllegalMonitorStateException.class, () -> myLock.unlock());
  }

  @Test
  void testUnlockByNonOwningThread() throws InterruptedException {
    myLock.lock();
    myLock.lock();

    Thread t = new Thread(() -> {
      assertThrows(IllegalMonitorStateException.class, () -> myLock.unlock());
    });
    t.start();
    t.join();
  }
}
