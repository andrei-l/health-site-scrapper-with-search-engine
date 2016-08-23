val springBootStarterVersion = "1.4.0.RELEASE"
val jacksonVersion = "2.7.5"
val swaggerVersion = "2.4.0"


lazy val springBootStarterWebDependency = ("org.springframework.boot" % "spring-boot-starter-web" % springBootStarterVersion)
  .exclude(jacksonCoreDatabindDependency.organization, jacksonCoreDatabindDependency.name)
lazy val springBootStarterDataElasticSearchDependency = ("org.springframework.boot" % "spring-boot-starter-data-elasticsearch" % springBootStarterVersion)
  .exclude(jacksonCoreDatabindDependency.organization, jacksonCoreDatabindDependency.name)

lazy val elasticSearchJNADependency = "net.java.dev.jna" % "jna" % "4.2.2"

lazy val swaggerSpringDependency = "io.springfox" % "springfox-swagger2" % swaggerVersion
lazy val swaggerSpringUIDependency = "io.springfox" % "springfox-swagger-ui" % swaggerVersion
lazy val scalaScrapperDependency = "net.ruippeixotog" %% "scala-scraper" % "1.0.0"

lazy val urlNormalizationDependency = "com.naytev" % "url-normalization_2.10" % "0.3"

lazy val jacksonModuleScalaDependency = "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
lazy val jacksonCoreDatabindDependency = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion

lazy val springBootTestStarterDependency = "org.springframework.boot" % "spring-boot-starter-test" % "1.4.0.RELEASE" % Test
lazy val jsonAssertDependency = "org.skyscreamer" % "jsonassert" % "1.3.0" % Test



name := "nhs-choices"
organization := "com.nhs.choices"
version := "1.0"
scalaVersion := "2.11.8"

oneJarSettings


libraryDependencies ++= Seq(
  springBootStarterWebDependency,
  springBootStarterDataElasticSearchDependency,

  elasticSearchJNADependency,

  swaggerSpringDependency,
  swaggerSpringUIDependency,
  jacksonModuleScalaDependency,
  jacksonCoreDatabindDependency,
  scalaScrapperDependency,

  urlNormalizationDependency,

  springBootTestStarterDependency,
  jsonAssertDependency
)