resolvers += "Typesafe repository".at("https://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.8.5")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"   % "1.6.1")
addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.12")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.4.2")
