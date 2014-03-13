organization in ThisBuild := "org.soqqo"

name := "io-straight-fw"

version in ThisBuild := "0.1"

scalacOptions in ThisBuild := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions") // , "-Ydebug", "-verbose","-Yissue-debug") // -Ydebug

scalaVersion in ThisBuild := Version.Scala

resolvers in ThisBuild ++= Seq( 
      "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
      "Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "twitter repo" at "http://maven.twttr.com",
      "typesafe releases repo" at "http://repo.typesafe.com/typesafe/releases",
      "apache snapshots" at "http://repository.apache.org/content/groups/snapshots",  // commons-csv
      "spray repo" at "http://repo.spray.io/"
)
