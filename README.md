# mini-refined

![example workflow](https://github.com/note/mini-refined/actions/workflows/ci.yml/badge.svg)

A proof of concept of a simple encoding of refinement types in Scala 3

## Quick start

Include library in `build.sbt`:

```
libraryDependencies += "pl.msitko" %% "mini-refined" % "0.1.0"
```

Common imports:

```scala
import pl.msitko.refined.auto._
import pl.msitko.refined.Refined
```

## Int predicates

```scala
val a: Int Refined GreaterThan[10] = 5
// fails compilation with: Validation failed: 5 > 10
```

```scala
val a: Int Refined LowerThan[10] = 15
// fails compilation with: Validation failed: 15 < 10
```

## String predicates

```scala
val s: String Refined StartsWith["xyz"] = "abc"
// fails compilation with: Validation failed: abc.startsWith(xyz)
```

```scala
val s: String Refined EndsWith["xyz"] = "abc"
// fails compilation with: Validation failed: abc.endsWith(xyz)
```

## List predicates

```scala
val as: List[String] Refined Size[GreaterThan[1]] = List("a")
// fails compilation with: 
// Validation failed: list size doesn't hold predicate: 1 > 1
```

You can use any `Int` predicates within `Size` predicate.

## Compose predicates with boolean operators

You can compose predicates with boolean operators. For example:

```scala
val c: Int Refined And[GreaterThan[10], LowerThan[20]] = 25
// fails compilation with: Validation failed: (25 > 10 And 25 < 20), predicate failed: 25 < 20
```

## Runtime validation

Everything described so far works only for values known at a compile-time. However, values for most variables are coming
at runtime. For those you need to use `Refined.refineV[T]` which returns `Either[String, T]`. Example:

```scala
case class Example(a: Int, b: Int Refined GreaterThan[10])

def runtime(a: Int, b: Int): Either[String, Example] =
  Refined.refineV[GreaterThan[10]](b).map(refined => Example(a, refined))
```

## Inferring types compatibility

`mini-refined` has some basic rules that enable using more specific types in places where more general types are required.

In other words, considering such function:

```scala
def intFun10(a: Int Refined GreaterThan[10]): Unit = ???
```

We can it with a value of type `Int Refined GreaterThan[20]` as `mini-refined` recognizes that being greater than 20 implies being greater than 10.
