Нет, этот counter не thread-safe, перепутаны lockY и lockX в методе get().
```java
static long x;
static long y;
static final Object lockX = new Object();
static final Object lockY = new Object();

public static void increment() {
  final boolean isX = Thread.currentThread().getId() % 2 == 0;
  final var lock = isX ? lockX : lockY;
  synchronized(lock) {
    if (isX) {
      x++;
    } else {
      y++;
    }
  }
}

public static void get() {
  long a;
  long b;
  synchronized(lockY) {
    a = y;
  }
  synchronized(lockX) {
    b = x;
  }
  return a + b;
}
```
