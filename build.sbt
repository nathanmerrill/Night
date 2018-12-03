name := "Night"

version := "1.0.0-prerelease"

scalaVersion := "2.12.4"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"

resolvers += "bintray-djspiewak-maven" at "https://dl.bintray.com/djspiewak/maven"


val ParsebackVersion = "0.3"

libraryDependencies += "com.codecommit" %% "parseback-core" % ParsebackVersion

libraryDependencies += "com.codecommit" %% "parseback-scalaz-72" % ParsebackVersion

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies += "com.lihaoyi" %% "fastparse" % "2.0.4"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value