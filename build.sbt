organization := "com.pellucid"

name := "case-config"

scalaVersion := "2.11.2"

//crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions ++= Seq(
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
