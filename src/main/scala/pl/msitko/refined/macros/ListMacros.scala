package pl.msitko.refined.macros

import quoted.*

object ListMacros:

  transparent inline def listSize[T](inline in: List[T]): Int =
    ${ listSizeCode('in) }

  def listSizeCode[T](in: Expr[List[T]])(using q: Quotes): Expr[Int] =
    import quotes.reflect.*
    def rec(tree: Term): Expr[Int] =
      tree match
        case Inlined(_, _, i: Inlined) =>
          rec(i)
        case Inlined(_, _, Apply(TypeApply(Select(Ident("List"), "apply"), _), List(Typed(Repeated(xs, _), _)))) =>
          Expr(xs.size)
        case Inlined(_, _, TypeApply(Select(Ident("List"), "empty"), _)) =>
          Expr(0)
        case Inlined(None, Nil, Ident("Nil")) =>
          Expr(0)
        case _ =>
          val treeStr = in.asTerm.show(using Printer.TreeStructure)
          q.reflect.report.throwError(s"Cannot determine size of list in compiletime. Tree: $treeStr")
    rec(in.asTerm)

  // TODO: Remove code duplication as it's basically the same as listSize
  transparent inline def listSizeString[T](inline in: List[T]): String =
    ${ listSizeStringCode('in) }

  def listSizeStringCode[T](in: Expr[List[T]])(using q: Quotes): Expr[String] =
    import quotes.reflect.*
    def rec(tree: Term): Expr[String] =
      tree match
        case Inlined(_, _, i: Inlined) =>
          rec(i)
        case Inlined(_, _, Apply(TypeApply(Select(Ident("List"), "apply"), _), List(Typed(Repeated(xs, _), _)))) =>
          Expr(xs.size.toString)
        case Inlined(_, _, TypeApply(Select(Ident("List"), "empty"), _)) =>
          Expr("0")
        case Inlined(None, Nil, Ident("Nil")) =>
          Expr("0")
        case _ =>
          val treeStr = in.asTerm.show(using Printer.TreeStructure)
          q.reflect.report.throwError(s"Cannot determine size of list in compiletime. Tree: $treeStr")
    rec(in.asTerm)
