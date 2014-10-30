organization in ThisBuild := "com.pellucid"

name in ThisBuild := "case-config"

scalaVersion in ThisBuild := "2.11.2"

//crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature", "-unchecked", "-language:higherKinds")

resolvers in ThisBuild ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies in ThisBuild <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)

libraryDependencies in ThisBuild ++= Seq(
  Dependencies.Compile.typesafeConfig,
  Dependencies.Test.scalaTest
)

def ccProject(name: String) =
  Project(name, file(s"modules/$name"))

lazy val root =
  Project("case-config-root", file("."))
    .aggregate(core)
    .aggregate(macros)

lazy val core =
  ccProject("core")
    .dependsOn(macros)

lazy val macros = ccProject("macros")