organization in ThisBuild := "org.soqqo"

name := "straight-io"

version in ThisBuild := "0.1"

scalacOptions in ThisBuild := Seq("-unchecked", "-deprecation", "-encoding", "utf8") // , "-Ydebug", "-verbose","-Yissue-debug") // -Ydebug

scalaVersion in ThisBuild := Version.Scala

resolvers in ThisBuild ++= Seq( 
      "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
      "Journalio Repo" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-releases",
      "spray repo" at "http://repo.spray.io",
      "Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

EclipseKeys.createSrc in ThisBuild := EclipseCreateSrc.Default + EclipseCreateSrc.Resource // + Seq("target/generated_sources/scala")

EclipseKeys.withSource in ThisBuild := true

// net.virtualvoid.sbt.graph.Plugin.graphSettings

libraryDependencies ++= Seq(
  "org.eligosource"                   %%  "eventsourced-core"                % Version.eventsourced          % "compile",
  // "org.eligosource"                   %%  "eventsourced-journal-leveldb"     % Version.eventsourced  % "compile",
  "org.eligosource"                   %%  "eventsourced-journal-journalio"   % Version.eventsourced  % "compile",
  "io.spray"                          %   "spray-servlet"                    % Version.Spray               % "compile",
  "io.spray"                          %   "spray-routing"                    % Version.Spray               % "compile",
  "io.spray"                          %   "spray-testkit"                    % Version.Spray               % "test",
  "io.spray"                          %   "spray-caching"                    % Version.Spray               % "compile",
  "org.springframework.security"      %   "spring-security-core"             % "3.1.3.RELEASE"        % "compile",
  "ch.qos.logback"                    %   "logback-classic"                  % "1.0.9"                % "compile",
  "com.typesafe.akka"                 %%  "akka-slf4j"                       % Version.Akka           % "compile",
  "org.eclipse.jetty"                 %   "jetty-webapp"                     % "8.1.7.v20120910"      % "compile",
  "org.eclipse.jetty.orbit"           %   "javax.servlet"                    % "3.0.0.v201112011016"  % "compile"  artifacts Artifact("javax.servlet", "jar", "jar"),
  "com.typesafe.akka"                 %%  "akka-actor"                       % Version.Akka           % "compile",
  "com.fasterxml.jackson.core"        %   "jackson-core"                     % "2.2.0-SNAPSHOT"       % "compile",
  "com.fasterxml.jackson.core"        %   "jackson-annotations"              % "2.2.0-SNAPSHOT"                % "compile",
  "com.fasterxml.jackson.core"        %   "jackson-databind"                 % "2.2.0-SNAPSHOT"                % "compile",
  "com.fasterxml.jackson.module"      %   "jackson-module-jaxb-annotations"  % "2.2.0-SNAPSHOT"                % "compile",
  "com.fasterxml.jackson.module"      %%  "jackson-module-scala"             % "2.2.0-SNAPSHOT"                % "compile",
  "com.fasterxml.jackson.dataformat"  %   "jackson-dataformat-xml"           % "2.2.0-SNAPSHOT"                % "compile",
  "com.fasterxml.jackson.datatype"    %   "jackson-datatype-joda"            % "2.2.0-SNAPSHOT"                % "compile",
  "org.apache.commons"                %   "commons-lang3"                    % "3.1"                  % "compile",
  "org.scalaz"                        %%  "scalaz-core"                      % "7.0.0-M9"                % "compile",
  "joda-time"                         %   "joda-time"                        % "2.1"                  % "compile",
  "org.joda"                          %   "joda-convert"                     % "1.2"                  % "compile",
  "org.joda"                          %   "joda-money"                       % "0.8"                  % "compile",
  "org.scala-stm"                     %%  "scala-stm"                        % "0.7"                  % "compile", // THIS library will become Scala Core.. check regularly
  "net.sandrogrzicic"                 %%  "scalabuff-runtime"                % "1.0.0"                % "compile",
  "com.typesafe.akka"                 %%  "akka-cluster-experimental"        % Version.Akka           % "test",
  "org.scala-lang"                    %   "scala-actors"                     % Version.Scala          % "test",
  "org.scalatest"                     %%  "scalatest"                        % "1.9.1"                % "test",
  "junit"                             %   "junit"                            % "4.8"                  % "test"
)

