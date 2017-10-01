name := "netscied-service"
organization := "tw.netscied"
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"
libraryDependencies += "net.liftweb" %% "lift-webkit" % "3.1.0-M1"