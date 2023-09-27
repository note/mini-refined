import Common._

lazy val miniRefined = (project in file("core"))
  .commonSettings("mini-refined")
  .settings(
    libraryDependencies ++= Dependencies.testDeps
  )

lazy val circeIntegration = (project in file("circe-integration"))
  .commonSettings("mini-refined-circe")
  .settings(
    libraryDependencies ++= Dependencies.circe ++ Dependencies.testDeps
  )
  .dependsOn(miniRefined)

// do not publish the root project
publish / skip := true
