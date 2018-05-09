Project boolexpr
========

boolexpr is a simple open-source library for creating and manipulating boolean expressions in Scala.

Example / Usage
====

A basic propositional expression is built out of the types `And`, `Or`, `Not`, `Var` and `Lit`.  All of these extend the base type Expression.  For example,

```scala
    val expr = And(Array(Var("A"),
                         Var("B"),
                         Or(Var("C"), Not(Var("C"))))
    println(expr);
```

We see the expression is what we expect:

```bash
((!C | C) & A & B)
```

### Input String Parsing ###
```
output:
```bash
(A & B)
true
```

### Converting to Disjunctive Normal (Sum-of-Products) Form ###

We can also convert expressions to sum-of-products form instead of just simplifying them.  For example:

```scala
    val nonStandard = Parser.parseString("((A | B) & (C | D))");
    println(nonStandard);

    val sopForm = RuleSet.toDNF(nonStandard);
    println(sopForm);
```
output:
```bash
((A | B) & (C | D))
((A & C) | (A & D) | (B & C) | (B & D))
```

### Converting to Conjunctive Normal (Product-of-Sums) form ###

Likewise, we can convert an expression to product-of-sums form.  For example:

```scala
    val nonStandard = Parser.parse("((A & B) | (C & D))");
    println(nonStandard);

    val posForm = BoolExpr.toCNF(nonStandard);
    println(posForm);

```
output:
```bash
((A & B) | (C & D))
((A | C) & (A | D) & (B | C) & (B | D))
```


Rules
====

Building
====

Development
====

boolexpr is being developed, and is not guaranteed to be stable or bug-free.  Bugs, suggestions, or pull requests are all very welcome.

License
====
Copyright 2017 Brian Rogoff

Licensed under the MIT License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0

