package org.nsu.syspro.parprog.stress.basic;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.III_Result;

@JCStressTest
@State
public class FindStates {
    volatile int x, y, z;

    @Actor
    public void a() {
        int a_x = x;   // A.1
        int a_z = z;   // A.2
        y = a_x + a_z; // A.3
    }

    @Actor
    public void b() {
        int b_x = x; // B.1
        x = b_x + 1; // B.2
        int b_z = z; // B.3
        z = b_z + 1; // B.4
    }

    @Actor
    public void c() {
        int c_y = y;    // C.1
        if (c_y == 2) { // C.2
            int c_x = x;  // C.3
            x = c_x - 1;  // C.4
        }
    }

    @Arbiter
    public void main(III_Result r) {
        r.r1 = x;
        r.r2 = y;
        r.r3 = z;
    }
}
