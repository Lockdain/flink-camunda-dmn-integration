# Apache Flink - Camunda DMN Integration Sample

## About
This is a simple test application that demonstrates a possible
DMN decision execution path using Apache Flink as a runtime.

## Usage 
While running the application consumes Kafka topics `dmn-xml` and `dmn-variables`. The first one
is used for DMN notation delivery, while the second one delivers variables.

Topic `dmn-xml`:

Example:
````
{
    "ruleId" : "testRule",
    "xmlStr" : "body as xml"
}
````
The second topic `dmn-variables` waits for the JSON-serialized `DmnVariables` class, that
contains a string representation of the DMN rule id and a specific
list with DMN variables in the following format:
```
key1->val1,key2->val2,key3->val3
```


Example:
```
{
  "ruleId" : "testRule",
  "mapStr" : "key1->val1,key2->val2"
}
```

## Run
- Run `docker-compose up` using docker-compose.yml at the `/docker`
- Open Akhq UI at `localhost:8089`
- Run the `StreamingDmnApp`
- Publish `dmn-xml.json` to the `dnm-xml` topic using UI (see console for the events)
- Publish `dmn-variables.json` to the `dmn-variables` topic using UI (see console for the events)
- See the console for an outcome

Flink WebUI is available at `localhost:8081`.