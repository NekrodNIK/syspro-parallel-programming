package org.nsu.syspro.parprog.examples;

import org.nsu.syspro.parprog.interfaces.Fork;
import org.nsu.syspro.parprog.interfaces.Philosopher;

import java.util.concurrent.atomic.AtomicLong;

public class DefaultPhilosopher implements Philosopher {

  private static final AtomicLong idProvider = new AtomicLong(0);
  public final long id;
  private long successfulMeals;

  public DefaultPhilosopher() {
    this.id = idProvider.getAndAdd(1);
    this.successfulMeals = 0;
  }

  @Override
  public long meals() {
    return successfulMeals;
  }

  @Override
  public void countMeal() {
    successfulMeals++;
  }

  public void onHungry(Fork left, Fork right) {
    Fork first;
    Fork second;

    if (System.identityHashCode(left) < System.identityHashCode(right)) {
      first = left;
      second = right;
    } else {
      first = right;
      second = left;
    }

    long deadline = System.currentTimeMillis() + 1000; // таймаут 1 секунда

    while (System.currentTimeMillis() < deadline) {
      synchronized (first) {
        // пробуем захватить вторую вилку без блокировки
        if (Thread.holdsLock(second)) {
          continue;
        }
        synchronized (second) {
          eat(left, right);
          return;
        }
      }
    }

    // если таймаут - используем глобальный лок
    synchronized (DefaultPhilosopher.class) {
      synchronized (first) {
        synchronized (second) {
          eat(left, right);
        }
      }
    }
  }

  @Override
  public String toString() {
    return "DefaultPhilosopher{" +
        "id=" + id +
        '}';
  }
}
