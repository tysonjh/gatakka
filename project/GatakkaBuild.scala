import sbt._
import Keys._
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

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
        libraryDependencies ++= Dependency.gatorDep)) configs(MultiJvm)

  lazy val client = Project(
    "client",
    file("client"),
    settings = Dependency.commonSettings ++
      Seq(resolvers := Dependency.resolvers,
        libraryDependencies ++= Dependency.clientDep)) configs(MultiJvm) dependsOn(gator)
}

object Dependency {
  lazy val scalaVer = "2.10.0"
  lazy val akkaVer = "2.1.0"
  lazy val gateVer = "7.1"

  lazy val resolvers = Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val commonSettings = Defaults.defaultSettings ++ multiJvmSettings ++ Seq(
    organization := "com.tysonhamilton",
    version := "0.1",
    scalaVersion := scalaVer,
    crossPaths   := false
  )

  lazy val multiJvmSettings = SbtMultiJvm.multiJvmSettings ++ Seq(
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    parallelExecution in Test := false,
    executeTests in Test <<=
      ((executeTests in Test), (executeTests in MultiJvm)) map {
        case ((_, testResults), (_, multiJvmResults))  =>
          val results = testResults ++ multiJvmResults
          (Tests.overall(results.values), results)
      }
  )

  lazy val commonDep = Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.9",
  // -- Akka dependencies
    "com.typesafe.akka" %% "akka-actor" % akkaVer,
    "com.typesafe.akka" %% "akka-cluster-experimental" % akkaVer,
    "com.typesafe.akka" %% "akka-remote" % akkaVer,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVer,
  // -- Akka test dependencies
    "com.typesafe.akka" %% "akka-testkit" % akkaVer % "test" ,
    "com.typesafe.akka" %% "akka-remote-tests-experimental" % akkaVer % "test" ,
    "org.scalatest" %% "scalatest" % "1.9" % "test",
    "junit" % "junit" % "4.5" % "test"
  )

  lazy val gatorDep = commonDep ++ Seq(
    "uk.ac.gate" % "gate-core" % gateVer
  )

  lazy val clientDep = commonDep ++ Seq(
  // -- None currently
  )

}

