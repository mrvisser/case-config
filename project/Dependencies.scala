import sbt._

object Dependencies {

  object Version {
    val scalaTest = "2.2.1"
    val typesafeConfig  = "1.2.1"
  }

  object Compile {
    val typesafeConfig = "com.typesafe" % "config" % "1.2.1" % "provided"
  }

  object Test {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  }
}