lazy val akkaHttpVersion = "10.4.0"
lazy val akkaVersion    = "2.7.0"
lazy val logbackVersion = "1.2.11"
lazy val scalaUuidVersion = "0.3.1"
lazy val scalaTestVersion = "3.2.9"


// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.anthology",
      scalaVersion    := "2.13.4"
    )),
    name := "My Akka HTTP Project",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % logbackVersion,
      "io.jvm.uuid"       %% "scala-uuid"               % scalaUuidVersion,

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion   % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion       % Test,
      "org.scalatest"     %% "scalatest"                % scalaTestVersion  % Test
    )
  )
