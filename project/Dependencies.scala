import sbt._

object Dependencies {
	val circeVersion = "0.14.1"

	lazy val circe = Seq(
		"io.circe" %% "circe-core",
		"io.circe" %% "circe-generic",
		"io.circe" %% "circe-parser" // TODO: remove?
	).map(_ % circeVersion)

	lazy val munit = "org.scalameta" %% "munit" % "0.7.29" % Test

	lazy val testDeps    = Seq(munit)
}
