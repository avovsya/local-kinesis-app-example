package localKinesis

import java.nio.charset.{CharacterCodingException, Charset}

import com.amazonaws.services.kinesis.clientlibrary.exceptions.{InvalidStateException, ShutdownException, ThrottlingException}
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2._
import com.amazonaws.services.kinesis.clientlibrary.types.{InitializationInput, ProcessRecordsInput, ShutdownInput, ShutdownReason}
import com.amazonaws.services.kinesis.model.Record

// Implicitly add map/flatMap/etc to Java iterators
import scala.collection.JavaConversions._

object RecordProcessorFactory extends IRecordProcessorFactory {
  override def createProcessor(): IRecordProcessor = {
    new RecordProcessor()
  }
}

class RecordProcessor extends IRecordProcessor {
  var shardId: String = ""

  override def initialize(initializationInput: InitializationInput): Unit = {
    println("Initializing record processor for shard: " + initializationInput.getShardId)

    shardId = initializationInput.getShardId
  }

  override def shutdown(shutdownInput: ShutdownInput): Unit = {
    println("Shutting down processor for: " + shardId)

    if (shutdownInput.getShutdownReason == ShutdownReason.TERMINATE) {
      checkpoint(shutdownInput.getCheckpointer)
    }
  }

  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit = {
    println("Received records")
    for {
      record <- processRecordsInput.getRecords.toList
    } yield processSingleRecord(record)
    println("Records processed successfully. Checkpointing...")
    checkpoint(processRecordsInput.getCheckpointer)
  }

  private def processSingleRecord(r: Record) = {
    println("Processing record")

    try {
      val data = Charset.forName("utf-8").decode(r.getData).toString
      println("RECEIVED RECORD: " + data)
    } catch {
      case cce: CharacterCodingException => println("Malformed data", cce)
    }
  }

  private def checkpoint(checkpointer: IRecordProcessorCheckpointer): Unit = {
    try {
      checkpointer.checkpoint()
    } catch {
      case te: ThrottlingException => println("Throttling exception while checkpointing: " + te)
      case se: ShutdownException => println("Shutdown exception while checkpointing: " + se)
      case ie: InvalidStateException => println("Invalid State exception while checkpointing: " + ie)
    }
  }
}
