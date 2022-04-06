package ru.asergeenko.dmn.flink

import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction
import org.apache.flink.util.Collector
import org.camunda.bpm.dmn.engine.{DmnDecision, DmnEngine, DmnEngineConfiguration}

class DmnExecutionFunction extends KeyedCoProcessFunction[String, DmnXml, DmnVariables, String]{
  implicit val typeInfo = TypeInformation.of(classOf[String])

  lazy val dmnCoreState = getRuntimeContext
     .getState(new ValueStateDescriptor[String]("dmn-decision", typeInfo))



  override def processElement1(dmnXml: DmnXml, ctx: KeyedCoProcessFunction[String, DmnXml, DmnVariables, String]#Context, out: Collector[String]): Unit = {
    println("New DMN definition is arrived.")

    dmnCoreState.update(dmnXml.xmlStr)

  }

  override def processElement2(dmnVariables: DmnVariables, ctx: KeyedCoProcessFunction[String, DmnXml, DmnVariables, String]#Context, out: Collector[String]): Unit = {
    println("New DMN variables are arrived.")

    val dmnEngine = DmnEngineConfiguration
      .createDefaultDmnEngineConfiguration()
      .buildEngine()

    val variableMap = Util.deserializeStringToDmnVars(dmnVariables.mapStr)
    val dmnDecision = dmnEngine.parseDecision(ctx.getCurrentKey, Util.strToStream(dmnCoreState.value())) // TODO: Move to state as DmnDecision object
    val outcome = Util.evaluateDecisionTable(dmnEngine, dmnDecision, variableMap)
    out.collect(outcome.toString)
  }
}


