#set page(paper: "a4")

#import "@preview/ctheorems:1.1.3": *
#show: thmrules.with(qed-symbol: $square$)

#let def = thmbox(
    "def",
    "Определение",
    titlefmt: strong,
    fill: none,
).with(numbering: none)

#let lemma = thmbox(
    "lemma",
    "Лемма",
    titlefmt: strong,
    fill: none,
).with(numbering: "1")
#let proof = thmproof("proof", "Доказательство")
#set text(font: "New Computer Modern")

```java
public class RegMRSWRegister implements Register<Byte> {
    private static int RANGE = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
    boolean[] r_bit = new boolean[RANGE]; // regular boolean MRSW
    public RegMRSWRegister(int capacity) {
        for (int i = 1; i < r_bit.length; i++)
            r_bit[i] = false;
        r_bit[0] = true;
    }
    
    public void write(Byte x) {
        r_bit[x] = true;
        for (int i = x - 1; i >= 0; i--)
            r_bit[i] = false;
    }
    
    public Byte read() {
        for (int i = 0; i < RANGE; i++)
            if (r_bit[i]) {
                return i;
            }
        return -1; // impossible
    }
}
```
#lemma[
    Regular M-Valued MRSW Register является wait-free
]
#proof[\
    `read()`
    - в цикле максимум `RANGE` итераций
    - на каждой итерации выполняет одно чтение и одно сравнение
    - в середине цикла или в конце метода `return`
    - итого максимум `2*RANGE+1` шагов
    `write()`:
    - `x < RANGE`
    - один шаг записи перед циклом
    - `x` шаг записи в цикле
    - итого максимум `RANGE` шагов
    Так как `RANGE` константа,
    то количество шагов конечно для каждого потока
]

#lemma[
    Вызов `read()` всегда возвращает значение соответствующее биту `0..RANGE-1`,
    установленное некоторым вызовом `write()`
] <inv>
#proof[\
    Инвариант:
    если поток читает `r_bit[j]`,
    то некоторый бит с индексом `j` или больше,
    записанный вызовом `write()`, установлен в `true`.
    
    При инициализации регистра, читателей ещё нет,
    а конструктор устанавливает `r_bit[0] = true`. Инвариант выполняется.

    Пусть поток читает `r_bit[j]` и $exists k >= j : $ `r_bit[k] is true`
    - если читатель переходит от `j` к `j+1`, то `r_bit[j] is false`, а значит `k > j`.
    - писатель очищает `r_bit[k]` только если устанавливает `r_bit[l]` в `true`, для `l > k`.
]
#lemma[
    Regular M-Valued MRSW Register является регулярным
]
#proof[
    Для любого чтения, пусть `x` значение,
    записаное самым последним не пересекающимся вызовом `write()`.
    Как только `write()` завершён, `r_bit[x] is true` и `r_bit[i] is false`, для `i < x`
    
    По @inv, если `read()` возвращает значение не равное `x`, то он наблюдал какой-то `r_bit[j]`, т.ч. `j != x`.
    Который должен быть установлен при помощи concurrent `write()`.
]
