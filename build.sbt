name := "Night"

version := "1.0.0-prerelease"

scalaVersion := "2.12.4"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"

resolvers += "bintray-djspiewak-maven" at "https://dl.bintray.com/djspiewak/maven"

val ParsebackVersion = "0.3"

libraryDependencies += "com.codecommit" %% "parseback-core" % ParsebackVersion

libraryDependencies += "com.codecommit" %% "parseback-cats" % ParsebackVersion
// or!
libraryDependencies += "com.codecommit" %% "parseback-scalaz-72" % ParsebackVersion
// or!
libraryDependencies += "com.codecommit" %% "parseback-scalaz-71" % ParsebackVersion
