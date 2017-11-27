name := "QA_Cinemas"
 
version := "1.0" 
      
lazy val `qa_cinemas` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test )
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play25"
libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "6.0.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test"
)