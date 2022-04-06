package ru.asergeenko.dmn.flink


import org.camunda.bpm.dmn.engine.{DmnDecision, DmnEngine}
import org.camunda.bpm.engine.variable.{VariableMap, Variables}

import java.io.{ByteArrayInputStream, InputStream}
import collection.JavaConversions._

object Util {
  def strToStream(str: String): InputStream = {
    val stream = new ByteArrayInputStream(str.getBytes())
    stream
  }

  def evaluateDecisionTable(dmnEngine: DmnEngine, dmnDecision: DmnDecision, varMap: VariableMap): Object = {
    val decisionTableResult = dmnEngine.evaluateDecisionTable(dmnDecision, varMap)
    val singleResult = decisionTableResult.getSingleResult().getSingleEntry()
    singleResult
  }

  // input string: "key1->val1,key2->val2"
  def deserializeStringToDmnVars(input: String) = {
    val variables = collection.mutable.Map[String, AnyRef]()
    val split = input.split(",").map(_.split("->"))
    split.foreach {
      elem =>
        variables.put(elem(0), elem(1))
        variables
    }
    Variables.fromMap(variables)
  }
}
