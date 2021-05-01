package pl.msitko.refined

sealed trait ValidateExpr

object ValidateExpr:
  class And[A <: ValidateExpr, B <: ValidateExpr] extends ValidateExpr
  class Or[A <: ValidateExpr, B <: ValidateExpr]  extends ValidateExpr
  abstract class IntLeaf                          extends ValidateExpr
  class LowerThan[T <: Int with Singleton]        extends IntLeaf
  class GreaterThan[T <: Int with Singleton]      extends IntLeaf
  abstract class StringLeaf                       extends ValidateExpr
  class StartsWith[T <: String with Singleton]    extends StringLeaf
  class EndsWith[T <: String with Singleton]      extends StringLeaf
