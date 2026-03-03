public class First {
  public static void main(String[] args) throws Exception {
    Thread B = new Thread(() -> {
      throw new RuntimeException("Exception from B");
    });

    Thread A = new Thread(() -> {
      B.start();
      try {
        B.join();
      } catch (InterruptedException e) {}
    });

    A.start();
    A.join();
  }
}
