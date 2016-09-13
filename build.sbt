name := "local-kinesis-app-example"

version := "0.0.1"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/com.amazonaws/amazon-kinesis-client
libraryDependencies += "com.amazonaws" % "amazon-kinesis-client" % "1.6.5"

fork := true
envVars := Map(
  "AWS_ACCESS_KEY_ID" -> "FAKE",
  "AWS_SECRET_ACCESS_KEY" -> "FAKE",
  "AWS_REGION" -> "FAKE",
  "AWS_CBOR_DISABLE" -> "true")

