import sbt._

object Dependencies {
	val circeVersion = "0.14.6"

	lazy val circe = Seq(
		"io.circe" %% "circe-core" % circeVersion,
		"io.circe" %% "circe-generic" % circeVersion % Test,
		"io.circe" %% "circe-parser" % circeVersion % Test
	)

	lazy val munit = "org.scalameta" %% "munit" % "0.7.29" % Test

	lazy val testDeps    = Seq(munit)
}
