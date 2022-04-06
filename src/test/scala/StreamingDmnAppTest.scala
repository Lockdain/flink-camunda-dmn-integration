import org.camunda.bpm.engine.variable.Variables
import org.scalatest.FunSuite
import ru.asergeenko.dmn.flink.{DmnVariables, DmnXml, StreamingDmnApp, Util}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.camunda.bpm.dmn.engine.DmnDecision

import scala.io.Source


class StreamingDmnAppTest extends FunSuite {
  test("StreamingAppStringToVariableMap") {
  //  StreamingDmnApp.extractVariableMapFromString("parameter1->value;parameter2->value")
  }

  test("VariablesTest") {
    val vars = Variables
      .putValue("1", 2)
      .putValue("test", 8)
    println(vars.toString)
   }

  test("VariablesFlatteningTest") {
    val string = "key1->2,key2->3,key3->test"
    val map = Util.deserializeStringToDmnVars(string)
    println(map)
    map
  }

  test("Generate Test DmnXml.json") {
    val xmlStr = Source.fromFile("src/main/resources/ex_1.dmn").getLines.toList.mkString
    val dmnXml = DmnXml("avg_salary", xmlStr)
    val asJson = dmnXml.asJson
    println(asJson)
  }

  test("Generate Test DmnVariables.json") {
    val dmnVariables = DmnVariables("avg_salary", "age->24,salary->28000")
    val asJson = dmnVariables.asJson
    println(asJson)
  }
}
