import sbt._

object Dependencies {
	lazy val munit = "org.scalameta" %% "munit" % "0.7.25" % Test

	lazy val testDeps    = Seq(munit)
}
