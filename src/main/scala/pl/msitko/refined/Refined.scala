package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.compiletime._
import pl.msitko.refined.compiletime.ValidateExprInt._
import pl.msitko.refined.compiletime.ValidateExprList.{And, Or}
import quoted._
import pl.msitko.refined.runtime as RT

import quoted.Expr

object auto:

  export compiletime.ValidateExprInt.{And as _, Or as _, *}
  export compiletime.ValidateExprString.{And as _, Or as _, *}
  export compiletime.ValidateExprList.{And as _, Or as _, *}

  type ValidateExpr = compiletime.ValidateExprInt | compiletime.ValidateExprString | compiletime.ValidateExprList

  type And[A <: ValidateExpr, B <: ValidateExpr] = (A, B) match
    case (compiletime.ValidateExprInt, compiletime.ValidateExprInt)       => compiletime.ValidateExprInt.And[A, B]
    case (compiletime.ValidateExprString, compiletime.ValidateExprString) => compiletime.ValidateExprString.And[A, B]
    case (compiletime.ValidateExprList, compiletime.ValidateExprList)     => compiletime.ValidateExprList.And[A, B]

  type Or[A <: ValidateExpr, B <: ValidateExpr] = (A, B) match
    case (compiletime.ValidateExprInt, compiletime.ValidateExprInt)       => compiletime.ValidateExprInt.Or[A, B]
    case (compiletime.ValidateExprString, compiletime.ValidateExprString) => compiletime.ValidateExprString.Or[A, B]
    case (compiletime.ValidateExprList, compiletime.ValidateExprList)     => compiletime.ValidateExprList.Or[A, B]

  implicit inline def mkValidatedInt[V <: Int & Singleton, E <: ValidateExprInt](v: V): Refined[V, E] =
    inline ValidateInt.validate[V, E] match
      case null => Refined.unsafeApply(v)
      case failMsg =>
        inline val wholePredicateMsg = ValidateInt.showPredicate[V, E]
        inline if wholePredicateMsg == failMsg then reportError("Validation failed: " + wholePredicateMsg)
        else reportError("Validation failed: " + wholePredicateMsg + ", predicate failed: " + failMsg)

  implicit inline def mkValidatedString[V <: String & Singleton, E <: ValidateExprString](v: V): Refined[V, E] =
    inline ValidateString.validate[V, E] match
      case null => Refined.unsafeApply(v)
      case failMsg =>
        inline val wholePredicateMsg = ValidateString.showPredicate[V, E]
        inline if wholePredicateMsg == ValidateString.validate[V, E] then
          reportError("Validation failed: " + wholePredicateMsg)
        else
          reportError(
            "Validation failed: " + wholePredicateMsg + ", predicate failed: " + ValidateString.validate[V, E])

  implicit inline def mkValidatedList[T, E <: ValidateExprList](inline v: List[T]): Refined[List[T], E] =
    inline ValidateList.validate[E](v) match
      case null    => Refined.unsafeApply(v)
      case failMsg => reportError("Validation failed: " + failMsg)

  implicit inline def intLowerThanInference[T <: Int & Singleton, U <: Int & Singleton](
      v: Int Refined LowerThan[T]): Int Refined LowerThan[U] =
    inline erasedValue[T] < erasedValue[U] match
      case _: true  => Refined.unsafeApply(v.value)
      case _: false => reportError("Cannot be inferred")

  implicit inline def intGreaterThanInference[T <: Int & Singleton, U <: Int & Singleton](
      v: Int Refined GreaterThan[T]): Int Refined GreaterThan[U] =
    inline erasedValue[T] > erasedValue[U] match
      case _: true  => Refined.unsafeApply(v.value)
      case _: false => reportError("Cannot be inferred")

  // hack around scala.compiletime.error limitation that its argument has to be a literal
  // See: https://github.com/lampepfl/dotty/issues/10315
  private inline def reportError(inline a: String): Nothing = ${ reportErrorCode('a) }

  private def reportErrorCode(a: Expr[String])(using q: Quotes): Nothing =
    q.reflect.report.throwError(a.valueOrError)

//  private transparent inline def stringEquals(inline a: String, inline b: String): Boolean = ${ stringEqualsCode('a, 'b) }
//
//  private def stringEqualsCode(a: Expr[String], b: Expr[String])(using q: Quotes): Expr[Boolean] =
//    import quotes.reflect.*
//    println("bazinga 002")
//    println(a.asTerm.show(using Printer.TreeStructure))
//    println(b.asTerm.show(using Printer.TreeStructure))
//    println(Expr.betaReduce(b).show)
//    val res = a.valueOrError == Expr.betaReduce(b).valueOrError
//    Expr(res)

//opaque type Refined[Underlying, ValidateExpr] = Underlying
// I couldn't make the following work with opaque type:
// val a: Refined[Int, GreaterThan[10]] = 186
// That worked well with opaque type:
// val a: Int Refined GreaterThan[10] = mkValidatedInt[16, GreaterThan[10]](16)

// T is covariant so `val a: Refined[Int, GreaterThan[10]] = 186` works as well as `val a: Refined[186, GreaterThan[10]] = 186`
// but not sure how important it's that the latter works
final class Refined[+T <: Refined.Base, P <: Refined.ValidateExprFor[T]] private (val value: T) extends AnyVal

//trait Refined[+Underlying, ValidateExpr]

object Refined:
  type Base = Int | String | List[Any]

  type ValidateExprFor[B <: Base] = B match
    case Int       => ValidateExprInt
    case String    => ValidateExprString
    case List[Any] => ValidateExprList

  // We cannot simply `implicit inline def mk...(): Refined` because inline and opaque types do not compose
  // Read about it here: https://github.com/lampepfl/dotty/issues/6802
  private[refined] def unsafeApply[T <: Int, P <: ValidateExprInt](i: T): T Refined P = new Refined[T, P](i)

  private[refined] def unsafeApply[T <: String, P <: ValidateExprString](i: T): T Refined P =
    new Refined[T, P](i)

  private[refined] def unsafeApply[T, P <: ValidateExprList](i: List[T]): List[T] Refined P = new Refined[List[T], P](i)
  implicit def unwrap[T <: Int, P <: ValidateExprInt](in: Refined[T, P]): T                 = in.value
  implicit def unwrap[T <: String, P <: ValidateExprString](in: Refined[T, P]): T           = in.value
  implicit def unwrap[X, T <: List[X], P <: ValidateExprList](in: Refined[T, P]): List[X]   = in.value

  inline def refineV[P <: ValidateExprInt]: RT.ValidateInt[P] =
    new RT.ValidateInt[P](RT.ValidateExprInt.fromCompiletime[P])
