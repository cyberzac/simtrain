name := """simtrain"""
organization := "simtrain"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"
val akkaVersion = "2.5.4"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "simtrain.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "simtrain.binders._"
