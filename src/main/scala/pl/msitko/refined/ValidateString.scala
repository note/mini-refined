package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr.{And, EndsWith, StartsWith}
import quoted.{Expr, Quotes}

object ValidateString:
  transparent inline def validate[V <: String & Singleton, E <: ValidateExpr]: Boolean =
    inline erasedValue[E] match
      case _: StartsWith[t] =>
        inline startsWith(constValue[V], constValue[t]) match
          case _: true => true
          case _: false =>
            error(
              "Validation failed: " + constValue[V] + ".startsWith(" + constValue[t] + ")"
            ) // ${erasedValue[V].toString} < ${erasedValue[t].toString}
      case _: EndsWith[t] =>
        inline endsWith(constValue[V], constValue[t]) match
          case _: true  => true
          case _: false => error("Validation failed: " + constValue[V] + ".endsWith(" + constValue[t] + ")")
      case _: And[a, b] =>
        inline validate[V, a] match
          case true =>
            inline validate[V, b] match
              case true => true


  private transparent inline def startsWith(inline v: String, inline pred: String): Boolean =
    ${ startsWithCode('v, 'pred)  }

  private def startsWithCode(v: Expr[String], pred: Expr[String])(using Quotes): Expr[Boolean] =
    val res = v.valueOrError.startsWith(pred.valueOrError)
    Expr(res)

  private transparent inline def endsWith(inline v: String, inline pred: String): Boolean =
    ${ endsWithCode('v, 'pred)  }

  private def endsWithCode(v: Expr[String], pred: Expr[String])(using Quotes): Expr[Boolean] =
    val res = v.valueOrError.endsWith(pred.valueOrError)
    Expr(res)

