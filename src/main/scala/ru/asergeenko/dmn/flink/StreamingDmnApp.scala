package ru.asergeenko.dmn.flink

import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.camunda.bpm.engine.variable.VariableMap
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

abstract class StreamingElement()
case class DmnXml(ruleId: String, xmlStr: String) extends StreamingElement
case class DmnVariables(ruleId: String, mapStr: String) extends StreamingElement

object StreamingDmnApp extends App {

  val conf = new Configuration
  val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)

  // checkpoint every 10 seconds
  env.getCheckpointConfig.setCheckpointInterval(10 * 1000) // 10 sec

  // use event time for the application
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  // configure watermark interval
  env.getConfig.setAutoWatermarkInterval(1000L)

  // ingest 'dmn-xml' from socket
  val dmnDefinitionStream: DataStream[DmnXml] = env
    .addSource(new SocketTextStreamFunction("127.0.0.1", 3333, ";", -1))
    .map { dmnAsJson =>
      val dmnXml = decode[DmnXml](dmnAsJson).getOrElse(DmnXml("", ""))
      dmnXml
    }
  dmnDefinitionStream.print("Dmn definition stream.")

  val dmnVariablesStream: DataStream[DmnVariables] = env
    .addSource(new SocketTextStreamFunction("127.0.0.1", 2222, ";", -1))
    .map { dmnVariables =>
      val variables = decode[DmnVariables](dmnVariables).getOrElse(DmnVariables("", null))
      variables
    }
  dmnDefinitionStream.print("DMN variables stream.")

  dmnDefinitionStream
    .connect(dmnVariablesStream)
    .keyBy("ruleId", "ruleId")
    .process(new DmnExecutionFunction)

  env.execute("DMN-Decision")

}




