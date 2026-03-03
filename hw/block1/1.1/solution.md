## First case
- Thread B terminated due to an uncaught exception
- Thread A waits for thread B to terminate
  and will terminate when thread B terminates due to an uncaught exception

## Second case
- Just like the first case, but thread C is added,
  which calls B.join() and then terminates because thread B has already terminated

## Third case
- Just like the second case, but thread D is added, which waits for thread A to terminate

