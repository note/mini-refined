package pl.msitko.refined

sealed trait ValidateExpr

object ValidateExpr:
  class And[A <: ValidateExpr, B <: ValidateExpr] extends ValidateExpr
  class Or[A <: ValidateExpr, B <: ValidateExpr]  extends ValidateExpr
  abstract class IntLeaf                          extends ValidateExpr
  class LowerThan[T <: Int & Singleton]           extends IntLeaf
  class GreaterThan[T <: Int & Singleton]         extends IntLeaf
  abstract class StringLeaf                       extends ValidateExpr
  class StartsWith[T <: String & Singleton]       extends StringLeaf
  class EndsWith[T <: String & Singleton]         extends StringLeaf
  abstract class ListLeaf                         extends ValidateExpr
  class Size[T <: IntLeaf]          extends ListLeaf
