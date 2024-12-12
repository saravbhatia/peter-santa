# kafka

## producer
SampleKafkaProducer

## consumer
SampleKafkaConsumer

## properties
```shell
kafka:
  enabled: true
  schema-registry: "http://dev-schema-registry-b.tripactions.tools:8081,http://dev-schema-registry-a.tripactions.tools:8081"
  bootstrap-servers: "http://b-1.dev-kafka.f94ast.c12.kafka.us-west-2.amazonaws.com:9092,http://b-2.dev-kafka.f94ast.c12.kafka.us-west-2.amazonaws.com:9092"
  groupId: ta_baseline_service
  listener-container-concurrency: 2
  maxPollRecords: 100
```