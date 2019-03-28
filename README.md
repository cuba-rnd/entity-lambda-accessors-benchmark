# entity-lambda-accessors-benchmark
Microbenchmark to compare different method invocation options throughput and execution time. You should use Java 8 or higher.  

We use four options:
* Native invocation
* Reflection API
* Pure MethodHandles
* LambdaMetafactory

We use caching to store method references in the entity. 

To run the benchmark, just type ```gradlew jmh``` in project folder. 

Results are generated to JSON file to ```/build/reports/jmh/result.json```.

You can view results using [this JMH viewer](http://jmh.morethan.io/). Just upload report file to the site and chart will be generated for you. 

