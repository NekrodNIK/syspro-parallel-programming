### x == 1, r_y == 0, r_z == 0:
$$
A.1 \rightarrow A.2 \rightarrow A.3 \rightarrow 
A.4 \rightarrow B.1 \rightarrow B.2 \rightarrow 
B.3 \rightarrow B.4 \rightarrow A.5 \rightarrow B.5
$$
### x == 2, r_y == 0, r_z == 1:
$$
A.1 \rightarrow A.2 \rightarrow  A.3 \rightarrow 
A.4 \rightarrow B.1 \rightarrow B.2 \rightarrow 
B.3 \rightarrow A.5 \rightarrow B.4 \rightarrow B.5
$$
### x == 1, r_y == 0, r_z == 1:
Impossible, proof:
- If `r_z == 1`, then `B.4` executed with `x == 1`, so $A.5 \rightarrow B.4$
- according to program order, $B.4 \rightarrow B.5$
- If `r_y == 0`, then `A.4` executed with `x == 0`, so $A.4 \rightarrow A.5$ and $A.4 \rightarrow B.5$
- Thus, $A.4 \rightarrow A.5 \rightarrow B.4 \rightarrow B.5$ 
- If `x == 1`, then only one increment executed. Since $A.5$ executed, $B.5$ not executed.

  But $A.5 \rightarrow B.5$ - contradiction.
**Q.E.D.**

 
