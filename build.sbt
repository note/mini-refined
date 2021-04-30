import Common._

lazy val root = (project in file("."))
  .commonSettings("mini-refined", "0.1.0")
  .settings(
    libraryDependencies ++= Dependencies.testDeps
  )
