package org.nsu.syspro.parprog;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.nsu.syspro.parprog.base.DefaultFork;
import org.nsu.syspro.parprog.base.DiningTable;
import org.nsu.syspro.parprog.examples.DefaultPhilosopher;
import org.nsu.syspro.parprog.helpers.TestLevels;
import org.nsu.syspro.parprog.interfaces.Fork;
import org.nsu.syspro.parprog.interfaces.Philosopher;

public class CustomSchedulingTest extends TestLevels {

    static final class CustomizedPhilosopher extends DefaultPhilosopher {
        @Override
        public void onHungry(Fork left, Fork right) {
            sleepMillis(this.id * 20);
            System.out.println(Thread.currentThread() + " " + this + ": onHungry");
            super.onHungry(left, right);
        }
    }

    static final class CustomizedFork extends DefaultFork {
        @Override
        public void acquire() {
            System.out.println(Thread.currentThread() + " trying to acquire " + this);
            super.acquire();
            System.out.println(Thread.currentThread() + " acquired " + this);
            sleepMillis(100);
        }
    }

    static final class CustomizedTable extends DiningTable<CustomizedPhilosopher, CustomizedFork> {
        public CustomizedTable(int N) {
            super(N);
        }

        @Override
        public CustomizedFork createFork() {
            return new CustomizedFork();
        }

        @Override
        public CustomizedPhilosopher createPhilosopher() {
            return new CustomizedPhilosopher();
        }
    }

    static final class TableWithOneSlow extends DiningTable<Philosopher, DefaultFork> {
        private boolean first = true;

        private static class SlowPhilosopher extends DefaultPhilosopher {
            public SlowPhilosopher() {
                super();
            }

            @Override
            public void eat(Fork f1, Fork f2) {
                f1.acquire();
                try {
                    f2.acquire();
                    try {
                        countMeal();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } finally {
                        f2.release();
                    }
                } finally {
                    f1.release();
                }
            }
        }

        public TableWithOneSlow(int N) {
            super(N);
        }

        @Override
        public DefaultFork createFork() {
            return new DefaultFork();
        }

        @Override
        public Philosopher createPhilosopher() {
            if (first) {
                first = false;
                return new SlowPhilosopher();
            } else {
                return new DefaultPhilosopher();
            }
        }
    }

    @EnabledIf("easyEnabled")
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    @Timeout(2)
    void testDeadlockFreedom(int N) {
        final CustomizedTable table = dine(new CustomizedTable(N), 1);
    }

    @EnabledIf("easyEnabled")
    @ParameterizedTest
    @ValueSource(ints = { 2, 3, 4, 5 })
    @Timeout(3)
    void testSingleSlow(int N) {
        final TableWithOneSlow table = dine(new TableWithOneSlow(N), 2);
        assertTrue(table.maxMeals() >= 1000);
    }
}
