#set page(paper: "a4")

#import "@preview/ctheorems:1.1.3": *
#show: thmrules.with(qed-symbol: $square$)

#let statement = thmbox(
    "statement",
    "Утверждение",
    titlefmt: strong,
    fill: none,
    stroke: 0.5pt,
    radius: 0pt,
).with(numbering: none)
#let proof = thmproof("proof", "Доказательство")
#set text(font: "New Computer Modern")

```java
static boolean flags = new boolean[2]; // initially zero

public void foo() {
    int i = ThreadId.get();                                      // F.1
    int j = 1 - i;                                               // F.2
    while (true) {                                               // F.3
        flags[i] = true;             // i would like to enter    // F.4
        if (flags[j] == false) {     // you don't                // F.5
            if (flags[i] == true) {  // my request was not reset // F.6
                break;               // i win                    // F.7
            }
        } else {
            // looks like we have a contention
            flags[i] = false; // retreat                         // F.8
            flags[j] = false; // forcibly reset competitor       // F.9
        }
    }
}
```

#statement[\
    метод `foo()`
    - не wait-free
    - не lock-free
    - obstruction-free
    - не starvation-free
    - не deadlock-free
]
#proof[
    пусть $T_0$, $T_1$ потоки c id 0 и 1 соответственно.
    
    Внутри цикла возможны такие последовательности исполнения, что
    $
    cases(
        T_0.F.4 -> T_1.F.4,
        T_1.F.4 -> T_0.F.4,
        delim: "["
    )
    ->
    cases(
        T_0.F.5 -> T_1.F.5,
        T_1.F.5 -> T_0.F.5,
        delim: "["
    )
    -> T_0.F.8 -> T_1.F.8
    $
    Если каждый раз внутри цикла последовательность будет таковой, то цикл никогда не завершится
    $=>$ foo не wait-free и не lock-free.

    Докажем, что `foo` - obstruction-free. Пусть поток $T_1$ остановлен. \
    Рассмотрим состояния переменной `flags`: \
    $
      &#(true, false)  &=>& F.4 => F.5 \& F.6 "is" #true => "reach" F.7 \
      &#(false, false) &=>& F.9 => F.4 => "reduces to" #(true, false) \
      &#(true, true)   &=>& F.4 => F.5 \& F.6 "is" #false
        => F.8 => F.9 "reduces to" #(false, false) \
      &#(false, true)  &=>& F.8 => F.9 => "reduces to" #(false, false) \
    $
    для всех случаев цикл завершается за конечное число шагов $=>$ `foo` obstruction-free.

    Вернёмся к последовательности исполнения,
    которая использовалась в качестве доказательства отсутвия wait-freedom и lock-freedom.\
    Поток $T_0$ ожидает $T_1$, а $T_1$ ожидает $T_0$. \
    Цикл в wait-for графе $=>$ deadlock $=>$ `foo` не deadlock-free $=>$ `foo` не starvation-free.

    #qedhere
]
