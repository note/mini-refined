package pl.msitko.refined.macros

import quoted.*

object ListMacros:
  transparent inline def listSize[T](inline in: List[T]): Int =
    ${ listSizeCode('in) }

  def listSizeCode[T](in: Expr[List[T]])(using q: Quotes): Expr[Int] =
    import quotes.reflect.*
    in.asTerm match
      case Inlined(_, _, Apply(TypeApply(Select(Ident("List"), "apply"), _), List(Typed(Repeated(xs, _), _)))) =>
        Expr(xs.size)
      case Inlined(_, _, TypeApply(Select(Ident("List"), "empty"), _)) =>
        Expr(0)
      case Inlined(None, Nil, Ident("Nil")) =>
        Expr(0)
      case _ =>
        val treeStr = in.asTerm.show(using Printer.TreeStructure)
        q.reflect.report.throwError(s"Cannot determine size of list in compiletime. Tree: $treeStr")
