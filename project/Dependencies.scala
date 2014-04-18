import sbt._

object Version {
  val Scala = "2.10.3"
  val Akka = "2.3.0"
  val Spray = "1.3.1"
  val Jackson = "2.3.2"
}

/**
 * Our Project Dependencies
 */
object Dependencies {

  val resolutionRepos = Seq(
    "spray repo" at "http://repo.spray.io/",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    // "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
    "twitter Repository" at "http://maven.twttr.com", // why am I using this ?
    "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  // core akka-persistence deps
  val akkaTestKit     = "com.typesafe.akka"  %% "akka-testkit"                 % Version.Akka
  val akkaPersistence = "com.typesafe.akka"  %% "akka-persistence-experimental" % Version.Akka 
  val akkaCluster     = "com.typesafe.akka"  %% "akka-cluster"                  % Version.Akka
  val akkaContrib     = "com.typesafe.akka"  %% "akka-contrib"                  % Version.Akka

  // core spray dependencies
  val sprayCan        = "io.spray"           %  "spray-can"                    % Version.Spray
  val sprayRouting    = "io.spray"           %  "spray-routing"                % Version.Spray
  val specs2          = "org.specs2"         %% "specs2"                       % "2.2.3" 

  //
  // Test Dependencies
  //
  val sprayTestKit    = "io.spray"           %  "spray-testkit"                % Version.Spray
  val scalatest       = "org.scalatest"      %% "scalatest"                    % "2.1.0"
  val scalaMock       = "org.scalamock"      %% "scalamock-scalatest-support"  % "3.0.1"
  val junit           = "junit"              %  "junit"                        % "4.8"
  val mockito         = "org.mockito"        %  "mockito-core"                 % "1.9.5"

  //
  // jackson JSON (Un)marshalling
  //
  val jacksonCore     =  "com.fasterxml.jackson.core"       % "jackson-core"                    % Version.Jackson
  val jacksonAnnon    =  "com.fasterxml.jackson.core"       % "jackson-annotations"             % Version.Jackson
  val jacksonDBind    =  "com.fasterxml.jackson.core"       % "jackson-databind"                % Version.Jackson
  val jacksonJaxb     =  "com.fasterxml.jackson.module"     % "jackson-module-jaxb-annotations" % Version.Jackson
  val jacksonScala    =  "com.fasterxml.jackson.module"     %% "jackson-module-scala"           % Version.Jackson
  val jacksonXml      =  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml"          % Version.Jackson
  val jacksonJoda     =  "com.fasterxml.jackson.datatype"   % "jackson-datatype-joda"           % Version.Jackson

  // 
  // general Scala dependencies
  //
  val scalaReflect   = "org.scala-lang"      %  "scala-reflect"                % Version.Scala
  val scalaz         = "org.scalaz"          %% "scalaz-core"                  % "7.0.5"
  val scalaStm       = "org.scala-stm"       %% "scala-stm"                    % "0.7"
  val akkaActor      =  "com.typesafe.akka"  %% "akka-actor"                   % Version.Akka

  //
  // Logging
  //
  val logback        = "ch.qos.logback"      % "logback-classic"               % "1.0.9"
  val akkaSlf4j      = "com.typesafe.akka"   %% "akka-slf4j"                   % Version.Akka
  val slf4jJcl       = "org.slf4j"           % "jcl-over-slf4j"                % "1.7.5"
  val slf4j       = "org.slf4j"           % "slf4j-api"                % "1.7.5"

  //
  // General Helper Libraries (Joda of course)
  //
  val commonsLang    = "org.apache.commons" % "commons-lang3"                  % "3.1"
  val jodaTime       = "joda-time"          % "joda-time"                      % "2.1"
  val jodaConvert    = "org.joda"           % "joda-convert"                   % "1.2"

}
