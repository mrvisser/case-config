import sbt._

object Dependencies {

  object Version {
    val macros = "2.0.1"
    val scalaTest = "2.2.1"
    val typesafeConfig  = "1.2.1"
  }

  object Compiler {
    val macroParadise = "org.scalamacros" % "paradise" % Version.macros cross CrossVersion.full
  }

  object Compile {
    val quasiQuotes = "org.scalamacros" %% "quasiquotes" % Version.macros cross CrossVersion.binary
    val typesafeConfig = "com.typesafe" % "config" % Version.typesafeConfig % "provided"
  }

  object Test {
    val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
  }
}