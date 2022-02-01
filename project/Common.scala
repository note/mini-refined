import com.softwaremill.SbtSoftwareMillCommon.commonSmlBuildSettings
import com.softwaremill.Publish.ossPublishSettings
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt.{Compile, Project, Test, TestFramework}

object Common {
  implicit class ProjectFrom(project: Project) {
    def commonSettings(nameArg: String): Project = project.settings(
      name := nameArg,
      organization := "pl.msitko",

      scalaVersion := "3.1.0",
      scalafmtOnCompile := true,

      commonSmlBuildSettings,
      ossPublishSettings,
      scalacOptions ++= Seq(
        "-Xfatal-warnings",
      ),
      Compile / console / scalacOptions ~= filterConsoleScalacOptions,
      Test / console / scalacOptions ~= filterConsoleScalacOptions,
      testFrameworks += new TestFramework("munit.Framework")
    )
  }

  val filterConsoleScalacOptions = { options: Seq[String] =>
    options.filterNot(Set(
      "-Xfatal-warnings",
      "-Werror",
      "-Wdead-code",
      "-Wunused:imports",
      "-Ywarn-unused:imports",
      "-Ywarn-unused-import",
      "-Ywarn-dead-code",
    ))
  }
}
