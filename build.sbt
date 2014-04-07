organization in ThisBuild := "io.straight.fw"

name := "io-straight-fw"

version in ThisBuild := "0.3d"

scalacOptions in ThisBuild := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions") // , "-Ydebug", "-verbose","-Yissue-debug") // -Ydebug

scalaVersion in ThisBuild := Version.Scala

resolvers in ThisBuild ++= Seq( 
      "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
      "twitter repo" at "http://maven.twttr.com", // why am I using this ?
      "typesafe releases repo" at "http://repo.typesafe.com/typesafe/releases",
      "spray repo" at "http://repo.spray.io/",
      "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
