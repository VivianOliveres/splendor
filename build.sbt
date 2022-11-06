ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.kensai"
ThisBuild / organizationName := "splendor"

resolvers += Resolver.mavenLocal
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val root = (project in file("."))
  .aggregate(engine)


val testVersion = "3.2.12"

val defaultSettings = Seq(libraryDependencies ++= {
  Seq(
    "org.scalactic" %% "scalactic" % testVersion,
    "org.scalatest" %% "scalatest" % testVersion % "test",
    "org.scalatest" %% "scalatest-flatspec" % testVersion % "test",
    "org.scalatestplus" %% "junit-4-13" % "3.2.12.0" % "test",
    "org.scalatestplus" %% "scalacheck-1-16" % "3.2.12.0" % "test",
    "org.typelevel" %% "cats-core" % "2.7.0"
  )
})
lazy val engine = (project in file("engine"))
  .settings(defaultSettings)
