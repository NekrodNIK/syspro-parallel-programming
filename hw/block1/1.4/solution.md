## 1
T - total time before enhancement \
e = 0.1 - proportion of total time spent on task \
k = 10000 - number of CPUs \
S - speedup 

S = 1 / ((1 - e) + e / k) = 1 / (0.9 + 0.00001) $\approx$ 1.111 (11%)
## 2
T_A, T_B, T - time before enchacement \
t_A, t_B, t - time after enchacement \
k - number of CPUs \
T_A = 50 min \
T_B = 250 min \
T = T_A + T_B = 300 min \
t = 100 min

k = T_B / (t - T_A) = 250 / 50 = 5 CPUs
## 3
task A - serial part \
task B - parallel part \
a - proportion of total time spent on execution task A \
b = 1-a - proportion of total time spent on execution task B \
k = 100 - number of CPUs \
S = 10 - speedup

1 / (a + (1 - a) / k) = S \
a = (1/10 - 1/100) * 100/99 = 9/99 \
b = 90/99 ($\approx$ 91%)

