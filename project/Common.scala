import com.softwaremill.SbtSoftwareMillCommon.commonSmlBuildSettings
import com.softwaremill.Publish.ossPublishSettings
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt._
import sbt.{Compile, Project, Test, TestFramework}
import xerial.sbt.Sonatype.GitHubHosting
import xerial.sbt.Sonatype.autoImport.{sonatypeCredentialHost, sonatypeProfileName, sonatypeProjectHosting, sonatypeRepository}

object Common {
  implicit class ProjectFrom(project: Project) {
    def commonSettings(nameArg: String): Project = project.settings(
      name := nameArg,
      organization := "pl.msitko",

      scalaVersion := "3.1.0",
      scalafmtOnCompile := true,

      commonSmlBuildSettings,
      ossPublishSettings ++ Seq(
        sonatypeProfileName := "pl.msitko",
        organizationHomepage := Some(url("https://github.com/note")),
        homepage := Some(url("https://github.com/note/mini-refined")),
        sonatypeProjectHosting := Some(
          GitHubHosting("note", name.value, "pierwszy1@gmail.com")
        ),
        licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
        developers := List(
          Developer(
            id = "note",
            name = "Michal Sitko",
            email = "pierwszy1@gmail.com",
            url = new URL("https://github.com/note")
          )
        ),
        sonatypeCredentialHost := "oss.sonatype.org",
      ),
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
