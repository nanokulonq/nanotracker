#!/bin/bash

/opt/kafka/bin/kafka-topics.sh --create --topic report-topic --bootstrap-server kafka:9092 --partitions 1 --replication-factor 1

echo "Topic report-topic created successfully"