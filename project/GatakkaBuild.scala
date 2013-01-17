import sbt._
import Keys._

/**
 * User: tysonjh
 * Date: 1/15/13
 * Time: 3:36 PM
 */

object GatakkaBuild extends Build {
  lazy val gateakka = Project (
    "gate-akka",
    file("."),
    settings = Dependency.commonSettings,
    aggregate = Seq(gator, client))

  lazy val gator = Project(
    "gator",
    file("gator"),
    settings = Dependency.commonSettings ++
      Seq(resolvers:= Dependency.resolvers,
        libraryDependencies ++= Dependency.gatorDep))

  lazy val client = Project(
    "client",
    file("client"),
    settings = Dependency.commonSettings ++
      Seq(resolvers := Dependency.resolvers,
        libraryDependencies ++= Dependency.clientDep)) dependsOn(gator)
}

object Dependency {
  lazy val scalaVer = "2.10.0"
  lazy val akkaVer = "2.1.0"
  lazy val gateVer = "7.1"

  lazy val resolvers = Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.tysonhamilton",
    version := "0.1",
    scalaVersion := scalaVer
  )

  lazy val commonDep = Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.9"
  )

  lazy val gatorDep = commonDep ++ Seq(
    "uk.ac.gate" % "gate-core" % "7.1",
    "com.typesafe.akka" %% "akka-actor" % "2.1.0",
    "com.typesafe.akka" %% "akka-cluster-experimental" % "2.1.0",
    "com.typesafe.akka" %% "akka-remote" % "2.1.0"
  )

  lazy val clientDep = commonDep ++ Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.1.0",
    "com.typesafe.akka" %% "akka-cluster-experimental" % "2.1.0",
    "com.typesafe.akka" %% "akka-remote" % "2.1.0"
  )

}

