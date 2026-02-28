#!/bin/bash

# Start Zookeeper
cd /zookeeper/conf
cp zoo_sample.cfg zoo.cfg
../bin/zkServer.sh start
cd -


# sleep 20 seconds
sleep 20


# Start Kafka server
cd /kafka/bin
./kafka-server-start.sh ../config/server.properties > ../kafka_output.log &
cd -

# Wait for Kafka server to start
sleep 25

# Start Kafka connector
cd /kafka/bin
./connect-standalone.sh ../config/connect-standalone.properties ../config/connect-debezium-postgresql.properties