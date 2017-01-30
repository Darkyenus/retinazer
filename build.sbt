val gdxVersion = "1.9.2"

organization := "darkyenus"

name := "retinazer"

version := "0.2.2"

javacOptions in compile ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-g")

libraryDependencies += "com.badlogicgames.gdx" % "gdx" % gdxVersion

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

autoScalaLibrary := false

crossPaths := false
