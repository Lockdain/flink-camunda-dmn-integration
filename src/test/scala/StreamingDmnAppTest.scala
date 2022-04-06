import org.camunda.bpm.engine.variable.Variables
import org.scalatest.FunSuite
import ru.asergeenko.dmn.flink.{StreamingDmnApp, Util}

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
}
