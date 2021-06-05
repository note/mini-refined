package pl.msitko.refined.compiletime

sealed trait ValidateExprInt

object ValidateExprInt:
  class And[A <: ValidateExprInt, B <: ValidateExprInt] extends ValidateExprInt
  class Or[A <: ValidateExprInt, B <: ValidateExprInt]  extends ValidateExprInt
  class LowerThan[T <: Int & Singleton]                 extends ValidateExprInt
  class GreaterThan[T <: Int & Singleton]               extends ValidateExprInt
