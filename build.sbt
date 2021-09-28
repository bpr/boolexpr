ThisBuild / scalaVersion := "2.13.6"

lazy val hello = (project in file("."))
  .settings(
    name := "boolexpr",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.7" % Test,
  )
