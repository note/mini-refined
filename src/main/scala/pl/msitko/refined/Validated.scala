package pl.msitko.refined

import pl.msitko.refined.RefinedLift.checkPredicateInt

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr
import pl.msitko.refined.ValidateExpr._
import quoted.{Expr, Quotes}

object RefinedLift:
  implicit inline def mkValidatedInt[V <: Int with Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline if checkPredicateInt[V, E]
      then Refined.unsafeApply(v)
      else error("Validation failed")

  transparent inline def checkPredicateInt[V <: Int with Singleton, E <: ValidateExpr]: Boolean =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline erasedValue[V] < erasedValue[t] match
          case _: true => true
          case _: false =>
            error(
              "Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]]
            ) // ${erasedValue[V].toString} < ${erasedValue[t].toString}
      case _: GreaterThan[t] =>
        inline erasedValue[V] > erasedValue[t] match
          case _: true  => true
          case _: false => error("Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]])
      case _: And[a, b] =>
        inline checkPredicateInt[V, a] match
          case true =>
            inline checkPredicateInt[V, b] match
              case true => true

  implicit inline def mkValidatedString[V <: String with Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline if checkPredicateString[V, E]
      then Refined.unsafeApply(v)
      else error("Validation failed")

  transparent inline def startsWith(inline v: String, inline pred: String): Boolean =
    ${ startsWithCode('v, 'pred)  }

  def startsWithCode(v: Expr[String], pred: Expr[String])(using Quotes): Expr[Boolean] =
    val res = v.valueOrError.startsWith(pred.valueOrError)
    Expr(res)

  transparent inline def checkPredicateString[V <: String with Singleton, E <: ValidateExpr]: Boolean =
    inline erasedValue[E] match
      case _: StartsWith[t] =>
        inline startsWith(constValue[V], constValue[t]) match
          case _: true => true
          case _: false =>
            error(
              "Validation failed: " + constValue[V] + ".startsWith(" + constValue[t] + ")"
            ) // ${erasedValue[V].toString} < ${erasedValue[t].toString}
      case _: EndsWith[t] =>
        inline constValue[V].endsWith(constValue[t]) match
          case _: true  => true
          case _: false => error("Validation failed: " + constValue[V] + " < " + constValue[t])
      case _: And[a, b] =>
        inline checkPredicateString[V, a] match
          case true =>
            inline checkPredicateString[V, b] match
              case true => true


opaque type Refined[+Underlying, ValidateExpr] = Underlying
object Refined:
  // We cannot simply `implicit inline def mk...(): Refined` because inline and opaque types do not compose
  // Read about it here: https://github.com/lampepfl/dotty/issues/6802
  private [refined] def unsafeApply[V <: Int with Singleton, E <: ValidateExpr](i: V): V Refined E = i
  private [refined] def unsafeApply[V <: String with Singleton, E <: ValidateExpr](i: V): V Refined E = i


object Example:
  @main def main(): Unit =
    import RefinedLift._
    val a: Int Refined GreaterThan[10] = RefinedLift.mkValidatedInt[16, GreaterThan[10]](16)
    val b: Int Refined And[GreaterThan[10], LowerThan[20]] = RefinedLift.mkValidatedInt[16, And[GreaterThan[10], LowerThan[20]]](16)
