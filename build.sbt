ThisBuild / scalaVersion := "2.13.10"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.kensai"
name := "splendor"

resolvers += Resolver.mavenLocal
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val root = (project in file("."))
  .aggregate(model, engine)

val testVersion = "3.2.12"

val defaultSettings = Seq(libraryDependencies ++= {
  Seq(
    "org.scalactic" %% "scalactic" % testVersion,
    "org.scalatest" %% "scalatest" % testVersion % "test",
    "org.scalatest" %% "scalatest-flatspec" % testVersion % "test",
    "org.scalatestplus" %% "junit-4-13" % "3.2.12.0" % "test",
    "org.scalatestplus" %% "scalacheck-1-16" % "3.2.12.0" % "test",
    "org.typelevel" %% "cats-core" % "2.7.0",
    "com.lihaoyi" %% "os-lib" % "0.8.1"
  )
})

val protoSettings = Seq(libraryDependencies ++= {
  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  )
},
  Compile / PB.targets := Seq(
    scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
  )
)
lazy val model = (project in file("model"))
  .settings(defaultSettings, protoSettings)

lazy val engine = (project in file("engine"))
  .settings(defaultSettings)
  .dependsOn(model % "test->test;compile->compile")

