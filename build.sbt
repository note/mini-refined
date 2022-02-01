import Common._

lazy val root = (project in file("."))
  .commonSettings("mini-refined")
  .settings(
    libraryDependencies ++= Dependencies.testDeps
  )
