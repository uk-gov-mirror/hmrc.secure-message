resolvers += Resolver.bintrayIvyRepo("hmrc", "sbt-plugin-releases")
resolvers += Resolver.bintrayRepo("hmrc", "releases")
resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("uk.gov.hmrc"       % "sbt-auto-build"        % "2.10.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-git-versioning"    % "2.1.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-distributables"    % "2.0.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-service-manager"   % "0.8.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"            % "2.7.5")
addSbtPlugin("com.iheart"        % "sbt-play-swagger"      % "0.10.2")
addSbtPlugin("com.lucidchart"    % "sbt-scalafmt"          % "1.16")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"         % "1.6.1")
addSbtPlugin("org.scalastyle"    % "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.wartremover"   % "sbt-wartremover"       % "2.4.13")
