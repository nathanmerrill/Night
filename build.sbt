name := "Night"

version := "1.0.0-prerelease"

scalaVersion := "2.12.4"

resolvers += "bintray-djspiewak-maven" at "https://dl.bintray.com/djspiewak/maven"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies += "com.lihaoyi" %% "fastparse" % "2.0.4"