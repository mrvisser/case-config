organization := "ca.mrvisser"

name := "case-config"

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4", "2.11.4")

scalacOptions ++= Seq(
  "-Xlint",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-language:experimental.macros"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)

libraryDependencies ++= Seq(
  Dependencies.Compile.typesafeConfig,
  Dependencies.Test.scalaTest
)

libraryDependencies := {
  // Only include macro paradise + quasi-quotes plugin for scala 2.10
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => libraryDependencies.value
    case Some((2, 10)) =>
      libraryDependencies.value ++ Seq(
        compilerPlugin(Dependencies.Compiler.macroParadise),
        Dependencies.Compile.quasiQuotes
      )
  }
}

unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala_${scalaBinaryVersion.value}"
