package pl.msitko.refined.compiletime

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.compiletime.ValidateExprString
import pl.msitko.refined.compiletime.ValidateExprString._

import quoted.{Expr, Quotes}

object ValidateString:

  transparent inline def validate[V <: String & Singleton, E <: ValidateExprString]: String | Null =
    inline erasedValue[E] match
      case _: StartsWith[t] =>
        inline startsWith(constValue[V], constValue[t]) match
          case _: true => null
          case _: false =>
            showPredicateV[E](constValue[V])
      case _: EndsWith[t] =>
        inline endsWith(constValue[V], constValue[t]) match
          case _: true => null
          case _: false =>
            showPredicateV[E](constValue[V])
      case _: And[a, b] =>
        // workaround for: https://github.com/lampepfl/dotty/issues/12715
        inline val res = validate[V, a]
        inline res match
          case null =>
            validate[V, b]
          case _ => res
      case _: Or[a, b] =>
        inline validate[V, a] match
          case null => null
          case _    => validate[V, b]

  private transparent inline def startsWith(inline v: String, inline pred: String): Boolean =
    ${ startsWithCode('v, 'pred) }

  private def startsWithCode(v: Expr[String], pred: Expr[String])(using Quotes): Expr[Boolean] =
    val res = v.valueOrError.startsWith(pred.valueOrError)
    Expr(res)

  private transparent inline def endsWith(inline v: String, inline pred: String): Boolean =
    ${ endsWithCode('v, 'pred) }

  private def endsWithCode(v: Expr[String], pred: Expr[String])(using Quotes): Expr[Boolean] =
    val res = v.valueOrError.endsWith(pred.valueOrError)
    Expr(res)

  transparent inline def showPredicate[V <: String & Singleton, E <: ValidateExprString]: String =
    showPredicateV[E](constValue[V])

  transparent inline def showPredicateV[E <: ValidateExprString](v: String): String =
    inline erasedValue[E] match
      case _: StartsWith[t] =>
        v + ".startsWith(" + constValue[t] + ")"
      case _: EndsWith[t] =>
        v + ".endsWith(" + constValue[t] + ")"
      case _: And[a, b] =>
        inline val aMsg = showPredicateV[a](v)
        inline val bMsg = showPredicateV[b](v)
        "(" + aMsg + " And " + bMsg + ")"
      case _: Or[a, b] =>
        inline val aMsg = showPredicateV[a](v)
        inline val bMsg = showPredicateV[b](v)
        "(" + aMsg + " Or " + bMsg + ")"
