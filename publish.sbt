
credentials in ThisBuild += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo in ThisBuild := Some("lib-releases-local" at "http://pellucid.artifactoryonline.com/pellucid/libs-releases-local")

publishMavenStyle in ThisBuild := true
