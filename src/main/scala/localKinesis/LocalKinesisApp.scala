package localKinesis

import java.net.InetAddress
import java.util.UUID

import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.{InitialPositionInStream, KinesisClientLibConfiguration, Worker}
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel

object LocalKinesisApp extends App {

  val KinesisEndpoint: String = "http://127.0.0.1:4567/"
  val DynamoDBEndpoint: String = "http://127.0.0.1:4568/"

  val ApplicationStreamName: String = "stream-name"
  val ApplicationName: String = "app-name"
  val PositionInStream: InitialPositionInStream = InitialPositionInStream.TRIM_HORIZON

  java.security.Security.setProperty("networkaddress.cache.ttl", "60")

  val credentialsProvider = new com.amazonaws.auth.DefaultAWSCredentialsProviderChain

  try {
    credentialsProvider.getCredentials
  } catch {
    case e: Throwable => throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
      + "Please make sure that your credentials file is at the correct "
      + "location (~/.aws/credentials), and is in valid format.", e);
  }

  val workerId: String = InetAddress.getLocalHost.getCanonicalHostName + ":" + UUID.randomUUID

  val kinesisClientLibConfiguration = new KinesisClientLibConfiguration(ApplicationName,
    ApplicationStreamName,
    credentialsProvider,
    workerId)
    .withInitialPositionInStream(PositionInStream)
    .withMetricsLevel(MetricsLevel.NONE)

  val kinesisClient: AmazonKinesisClient = new AmazonKinesisClient().withEndpoint(KinesisEndpoint)
  val dynamoClient:AmazonDynamoDBClient = new AmazonDynamoDBClient().withEndpoint(DynamoDBEndpoint)

  val recordProcessorFactory: IRecordProcessorFactory = RecordProcessorFactory
  val worker: Worker = new Worker.Builder()
    .recordProcessorFactory(recordProcessorFactory)
    .config(kinesisClientLibConfiguration)
    .kinesisClient(kinesisClient)
    .dynamoDBClient(dynamoClient)
    .build()

  println(s"Running ${ApplicationName} to process stream ${ApplicationStreamName} as worker ${workerId}...\n")

  var exitCode = 0
  try {
    worker.run()
  } catch {
    case e: Throwable => {
      println("Caught throwable while processing data.")
      e.printStackTrace()
      exitCode = 1
    }
  }
  System.exit(exitCode)
}
