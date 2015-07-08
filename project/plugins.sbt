resolvers ++= Seq(
  "sbt-plugin-releases-repo" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"
)

// https://github.com/jrudolph/sbt-dependency-graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.gu" % "sbt-teamcity-test-reporting-plugin" % "1.5")

addSbtPlugin("org.scala-sbt.plugins" % "sbt-onejar" % "0.8")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")
