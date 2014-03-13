import sbt._
import Keys._

object Settings {
  val projectName = "ete"
  val buildSettings = Defaults.defaultSettings
}

object Version {
  val Scala = "2.10.2"
  val Akka = "2.3.0"
  val Spray = "1.3.0"
  val Jackson = "2.2.1"
}


object StraightIOBuild extends Build {
  import Settings._
  lazy val straightIOProject = Project( 
                               id = "straight-io", 
                               base = file("."),
                               settings = defaultSettings
                             ) 
}


/**
 * The SBT Build Object
 */
object RootBuild extends Build {

  import Settings._
  import Dependencies._

  lazy val root = Project(
          id = "io-straight-fw", 
          base = file("."))
          settings = defaultSettings
   )
   .settings(libraryDependencies ++= commonDeps ++ commonDeps ++ sprayDeps ++ akkaPersistence ++ jackson ++ subcut ++ googleApiDep)
}

/**
 * Our Project Dependencies
 */
object Dependencies {

  val googleApiDep = Seq(
    "com.google.api.client" % "google-api-client" % "1.4.1-beta" % "compile"
  )

  val sprayDeps =
      Seq(
        "io.spray"            %   "spray-can"     % Version.Spray,
        "io.spray"            %   "spray-routing" % Version.Spray,

        "org.specs2"          %%  "specs2"        % "2.2.3" % "test",
        "io.spray"            %   "spray-testkit" % Version.Spray  % "test",
        "com.typesafe.akka"   %%  "akka-testkit"  % Version.Akka   % "test"

      )

  val akkaPersistence = Seq(
    // akka persistence
    "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.0-RC4" % "compile"
  )
  val subcut = Seq(
    "com.escalatesoft.subcut" %% "subcut" % "2.0" % "compile"
  )

  val jackson = Seq(
    //
    // JSON (Un)marshalling
    //
    "com.fasterxml.jackson.core" % "jackson-core" % Version.Jackson % "compile",
    "com.fasterxml.jackson.core" % "jackson-annotations" % Version.Jackson % "compile",
    "com.fasterxml.jackson.core" % "jackson-databind" % Version.Jackson % "compile",
    "com.fasterxml.jackson.module" % "jackson-module-jaxb-annotations" % Version.Jackson % "compile",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % Version.Jackson % "compile",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % Version.Jackson % "compile",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % Version.Jackson % "compile"
  )

  val commonDeps = Seq(

    // 
    // general Scala dependencies
    //
    "org.scala-lang" % "scala-reflect" % Version.Scala % "compile",
    "org.scalaz" %% "scalaz-core" % "7.0.5" % "compile",
    "com.typesafe.akka" %% "akka-actor" % Version.Akka % "compile",
    "org.scala-stm" %% "scala-stm" % "0.7" % "compile",

    //
    // Logging
    //
    "ch.qos.logback" % "logback-classic" % "1.0.9" % "compile",
    "com.typesafe.akka" %% "akka-slf4j" % Version.Akka % "compile",
    "com.typesafe.akka" %% "akka-actor" % Version.Akka % "compile",

    "org.slf4j" % "jcl-over-slf4j" % "1.7.5" % "compile",

    //
    // General Helper Libraries (Joda of course)
    //
    "org.apache.commons" % "commons-lang3" % "3.1" % "compile",
    "joda-time" % "joda-time" % "2.1" % "compile",
    "org.joda" % "joda-convert" % "1.2" % "compile",
    "org.joda" % "joda-money" % "0.8" % "compile",

    //
    // Test Dependencies
    //
    "org.scala-lang" % "scala-actors" % Version.Scala % "test",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
    "junit" % "junit" % "4.8" % "test"

    /*
     * Setup our excluded jars 
     */
    excludeAll(
       ExclusionRule(organization = "commons-logging")
    )
  )
}
