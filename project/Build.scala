import sbt._
import Keys._

object Settings {
  val defaultSettings = Defaults.defaultSettings ++ Seq(
    scalacOptions in Compile ++= Seq("-target:jvm-1.6", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions"),
    javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6"),
    parallelExecution in Test := false
  )
}

object Version {
  val Scala =  "2.10.1"
  val Spray  = "1.1-M7"
  val Akka  =  "2.1.2"
  val eventsourced = "0.5-M2"

}

object StraightIOBuild extends Build {
  import Settings._
  lazy val straightIOProject = Project( 
                               id = "straight-io", 
                               base = file("."),
                               settings = defaultSettings
                             ) 
}
