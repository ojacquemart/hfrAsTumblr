import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "hfrastumblr"
  val appVersion      = "1.2.0-SNAPSHOT"

  val mainDeps = Seq(
    cache,
    "com.newrelic.agent.java"   % "newrelic-agent"          % "2.21.3",
    "org.jsoup"                 % "jsoup"                   % "1.7.2",
    "org.mindrot"               % "jbcrypt"                 % "0.3m",
    "org.reactivemongo"         %% "play2-reactivemongo"    % "0.10.0-SNAPSHOT",
    "play-autosource"           %% "reactivemongo"          % "1.0-SNAPSHOT"
  )

  val cacheDeps = Seq(cache)
  lazy val twitter = play.Project(appName + "-twitter", appVersion, cacheDeps, path = file("modules/twitter"))

  lazy  val main = play.Project(appName, appVersion, mainDeps).settings(
    resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/",
    resolvers += "Mandubian repository releases"  at "https://github.com/mandubian/mandubian-mvn/raw/master/releases/"
  ).dependsOn(twitter).aggregate(twitter)

}