### Proof `z = 1`
- starts at `z = 0`
- `z` is modified only at `B.4` => `z = 1`

### Proof `0 <= x <= 1`
- starts at `x = 0`  
- `B.1 -> B.2` increments `x`  
- `C.3 -> C.4` decrements `x`  
- if `B.2 -> C.3` or `C.4 -> B.1` => `x = 0`
- else if `C.3 -> B.2` and `B.1 -> C.4` and `C.4 -> B.2` => `x = 1`  
- else not possible

### Proof `0 <= y <= 2`
- starts at `y = 0`  
- `A.3` modify `y`: `y = a_x + a_z`  
- `0 <= x <= 1` and `a_x = x` => `0 <= a_x <= 1`  
- `0 <= z <= 1` and `a_z = z` => `0 <= a_z <= 1`  
- thus, `0 <= y = a_x + a_z <= 2`

### Proof of impossibility `(x = 0, y != 2)`
- `x = 0` => `C.4` is executed => `y = 2`

### Results
- `(x=1, y=0, z=1)` + `A.1 -> A.2 -> A.3 -> B.1 -> B.2 -> B.3 -> B.4 -> C.1 -> C.2`  
- `(x=1, y=1, z=1)` + `A.1 -> B.1 -> B.2 -> B.3 -> B.4 -> A.2 -> A.3 -> C.1 -> C.2`  
- `(x=1, y=2, z=1)` + `C.1 -> C.2 -> B.1 -> B.2 -> B.3 -> B.4 -> A.1 -> A.2 -> A.3`  
- `(x=0, y=2, z=1)` + `B.1 -> B.2 -> B.3 -> B.4 -> A.1 -> A.2 -> A.3 -> C.1 -> C.2 -> C.3 -> C.4`
