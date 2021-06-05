package pl.msitko.refined.compiletime

sealed trait ValidateExprString

object ValidateExprString:
  class And[A <: ValidateExprString, B <: ValidateExprString] extends ValidateExprString
  class Or[A <: ValidateExprString, B <: ValidateExprString]  extends ValidateExprString
  class StartsWith[T <: String & Singleton]                   extends ValidateExprString
  class EndsWith[T <: String & Singleton]                     extends ValidateExprString
