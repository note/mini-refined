package pl.msitko.refined.circe

import io.circe.{Decoder, Encoder}
import pl.msitko.refined.Refined

// TODO: Find a way to encode those codecs generically instead of defining them for each supported type
// Things like the following are not getting picked up by implicits mechanism, i.e. they work only when given is called explicitly:
// given [T : Decoder, P <: Refined.ValidateExprFor[T]]: Encoder[T Refined P] = ???

given intEncoder[P <: Refined.ValidateExprFor[Int]](using Encoder[Int]): Encoder[Int Refined P] =
  summon[Encoder[Int]].contramap(_.value)

inline given intDecoder[P <: Refined.ValidateExprFor[Int]](using Decoder[Int]): Decoder[Int Refined P] =
  summon[Decoder[Int]].emap(v => Refined.refineV[P](v))

given stringEncoder[P <: Refined.ValidateExprFor[String]](using Encoder[String]): Encoder[String Refined P] =
  summon[Encoder[String]].contramap(_.value)

inline given stringDecoder[P <: Refined.ValidateExprFor[String]](using Decoder[String]): Decoder[String Refined P] =
  summon[Decoder[String]].emap(v => Refined.refineV[P](v))

given listEncoder[T, P <: Refined.ValidateExprFor[List[Any]]](using Encoder[List[T]]): Encoder[List[T] Refined P] =
  summon[Encoder[List[T]]].contramap(_.value)

inline given listDecoder[T, P <: Refined.ValidateExprFor[List[Any]]](using
    Decoder[List[T]]): Decoder[List[T] Refined P] =
  summon[Decoder[List[T]]].emap(v => Refined.refineV[T, P](v))
