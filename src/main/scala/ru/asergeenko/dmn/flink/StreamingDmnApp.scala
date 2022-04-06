package ru.asergeenko.dmn.flink

import io.circe.generic.auto._
import io.circe.parser._
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}

import java.util.Properties

abstract class StreamingElement()
case class DmnXml(ruleId: String, xmlStr: String) extends StreamingElement
case class DmnVariables(ruleId: String, mapStr: String) extends StreamingElement

object StreamingDmnApp extends App {

  val conf = new Configuration
  val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", "localhost:9092")
  properties.setProperty("group.id", "dmn-streaming")
  properties.setProperty("auto.offset.reset", "latest")

  val producer = new FlinkKafkaProducer[String](
    "my-topic",                  // target topic
    new SimpleStringSchema(),    // serialization schema
    properties                // producer config
  )

  // checkpoint every 10 seconds
  env.getCheckpointConfig.setCheckpointInterval(10 * 1000) // 10 sec

  // use event time for the application
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  // configure watermark interval
  env.getConfig.setAutoWatermarkInterval(1000L)

  // ingest 'dmn-xml' from socket
  val dmnDefinitionStream: DataStream[DmnXml] = env
    .addSource(new FlinkKafkaConsumer[String]("dmn-xml", new SimpleStringSchema(), properties)
      .setStartFromLatest()
    )
    .map { dmnAsJson =>
      val dmnXml = decode[DmnXml](dmnAsJson).getOrElse(DmnXml("", ""))
      dmnXml
    }
    .setParallelism(1)
  dmnDefinitionStream.print("Dmn definition stream.")

  val dmnVariablesStream: DataStream[DmnVariables] = env
    .addSource(new FlinkKafkaConsumer[String]("dmn-variables", new SimpleStringSchema(), properties)
      .setStartFromLatest()
    )
    .map { dmnVariables =>
      val variables = decode[DmnVariables](dmnVariables).getOrElse(DmnVariables("", null))
      variables
    }
    .setParallelism(1)
  dmnDefinitionStream.print("DMN variables stream.")

  dmnDefinitionStream
    .connect(dmnVariablesStream)
    .keyBy("ruleId", "ruleId")
    .process(new DmnExecutionFunction)
    .addSink(producer)
//    .print("Decision result: ")
    .setParallelism(1)


  env.execute("DMN-Decision")

}




