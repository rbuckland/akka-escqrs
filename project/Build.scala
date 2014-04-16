import sbt._
import Keys._



/**
 * The SBT Build Object
 */
object Build extends Build {

  lazy val publishM2Configuration =
    TaskKey[PublishConfiguration]("publish-m2-configuration",
      "Configuration for publishing to the .m2 repository.")

  lazy val publishM2 =
    TaskKey[Unit]("publish-m2",
      "Publishes artifacts to the .m2 repository.")

  lazy val m2Repo =
    Resolver.file("publish-m2-local",
      Path.userHome / ".m2" / "repository")

  publishM2Configuration <<= (packagedArtifacts, checksums in publish, ivyLoggingLevel) map { (arts, cs, level) =>
    Classpaths.publishConfig(arts, None, resolverName = m2Repo.name, checksums = cs, logging = level)
  }

  publishM2 <<= Classpaths.publishTask(publishM2Configuration, deliverLocal)

  otherResolvers += m2Repo

  import BuildSettings._
  import Dependencies._

  lazy val root = Project(id = "root", base = file("."))
   .aggregate(akkaEscqrsCore,akkaEscqrsSpraySupport,akkaEscqrsWithScalazValidation)
   .settings(basicSettings: _*)

  lazy val akkaEscqrsCore = Project(id = "akka-escqrs-core", base = file("akka-escqrs-core"))
    .settings(commonModuleSettings: _*)
    .settings(libraryDependencies ++=
      compile(akkaActor, akkaPersistence, akkaCluster, akkaContrib, scalaReflect, scalaStm, jodaTime, commonsLang) ++
      test(scalatest) ++
      runtime(akkaSlf4j, logback)
    )

  lazy val akkaEscqrsSpraySupport = Project(id = "akka-escqrs-spray-support", base = file("akka-escqrs-spray-support"))
   .dependsOn(akkaEscqrsCore)
   .settings(commonModuleSettings: _*)
   .settings(libraryDependencies ++=
      compile(sprayRouting, jacksonCore, jacksonAnnon, jacksonDBind, jacksonJaxb, jacksonScala, jacksonXml, jacksonJoda) ++
      test(scalatest) ++
      provided(slf4j) ++
      runtime(akkaSlf4j, logback)
    )

  lazy val akkaEscqrsWithScalazValidation = Project(id = "akka-escqrs-validation-scalaz", base = file("akka-escqrs-validation-scalaz"))
    .dependsOn(akkaEscqrsCore)
    .settings(commonModuleSettings: _*)
    .settings(libraryDependencies ++=
    compile(scalaz) ++
      test(scalatest) ++
      provided(slf4j,sprayRouting) ++
      runtime(akkaSlf4j, logback)
    )


}

