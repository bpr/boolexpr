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

Alternatively, we could have provided our expression as a String in prefix notation and parsed it.  We can verify that this expression is identical to the one we built manually:

```scala
    Expression<String> parsedExpression = RuleSet.simplify(ExprParser.parse("( ( (! C) | C) & A & B)"));
    println(parsedExpression);
    System.out.println(parsedExpression.equals(simplified));
```
output:
```bash
(A & B)
true
```

### Converting to Disjunctive Normal (Sum-of-Products) Form ###

We can also convert expressions to sum-of-products form instead of just simplifying them.  For example:

```java
    Expression<String> nonStandard = ExprParser.parse("((A | B) & (C | D))");
    System.out.println(nonStandard);

    Expression<String> sopForm = RuleSet.toDNF(nonStandard);
    System.out.println(sopForm);
```
output:
```bash
((A | B) & (C | D))
((A & C) | (A & D) | (B & C) | (B & D))
```

### Converting to Conjunctive Normal (Product-of-Sums) form ###

Likewise, we can convert an expression to product-of-sums form.  For example:

```java
    Expression<String> nonStandard = ExprParser.parse("((A & B) | (C & D))");
    System.out.println(nonStandard);

    Expression<String> posForm = RuleSet.toCNF(nonStandard);
    System.out.println(posForm);

```
output:
```bash
((A & B) | (C & D))
((A | C) & (A | D) & (B | C) & (B | D))
```


All of these examples can also be found in [ExampleRunner](https://github.com/bpodgursky/jbool_expressions/blob/master/src/main/java/com/bpodgursky/jbool_expressions/example/ExampleRunner.java)

Rules
====

The current simplification rules define fairly simple and fast optimizations, and is defined in [RuleSet](https://github.com/bpodgursky/jbool_expressions/blob/master/src/main/java/com/bpodgursky/jbool_expressions/rules/RuleSet.java).
I'm happy to add more sophisticated rules (let me know about them via a PR or issue).  The current rules include:

Literal removal:

```bash
(false & A) => false
(true & A) => A

(false | A) => A
(true | A) => true
```

Negation simplification:

```bash
(!!A ) => A
(A & !A) => false
(A | !A) => true
```

And / Or de-duplication and flattening:

```bash
(A & A & (B & C)) => (A & B & C)
(A | A | (B | C)) => (A | B | C)
```

Child expression simplification:

```bash
(A | B) & (A | B | C) => (A | B)
((A & B) | (A & B & C)) => (A & B)
```

Additional rules for converting to sum-of-products form:

Propagating &:

```bash
( A & ( C | D)) => ((A & C) | (A & D))
```

De Morgan's law:

```bash
(! ( A | B)) => ( (! A) & (! B))
```

Building
====

boolexpr is built with Maven.  To build from source,

```bash
> mvn package
```

generates a snapshot jar target/jbool_expressions-1.0-SNAPSHOT.jar.

To run the test suite locally,

```bash
> sbt run
```

Development
====

boolexpr is being developed, and is not guaranteed to be stable or bug-free.  Bugs, suggestions, or pull requests are all very welcome.

License
====
Copyright 2017 Brian Rogoff

Licensed under the MIT License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0

