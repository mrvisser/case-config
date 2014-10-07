organization := "com.pellucid"

name := "case-config"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:higherKinds")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)

libraryDependencies ++= Seq(
  "com.typesafe"  %   "config"      % "1.2.1",
  "org.scalatest" %%  "scalatest"   % "2.2.1" % "test"
)
