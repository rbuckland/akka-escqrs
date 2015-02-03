import sbt._
import Keys._

object BuildSettings {
  val VERSION = "0.5.4"

  lazy val basicSettings = seq(
    version               := VERSION,
    homepage              := Some(new URL("https://github.com/rbuckland/akka-escqrs")),
    organization          := "io.straight.escqrs",
    organizationHomepage  := Some(new URL("https://github.com/rbuckland/akka-escqrs")),
    description           := "Helper classes, traits and objects for building an ES / CQRS application " +
                             "on top of Akka Persistencei - the Samples utilise DDD",
    startYear             := Some(2013),
    licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := Version.Scala,
    resolvers             ++= Dependencies.resolutionRepos,
    scalacOptions         := Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-deprecation",
      "-target:jvm-1.7",
      "-language:_",
      "-Xlog-reflective-calls"
    )
  )

  lazy val commonModuleSettings =
    basicSettings ++ 
    net.virtualvoid.sbt.graph.Plugin.graphSettings
}
