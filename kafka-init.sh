#!/bin/bash

KAFKA_CLUSTER_ID="$(/opt/kafka/bin/kafka-storage.sh random-uuid)"

/opt/kafka/bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c /opt/kafka/config/kraft/server.properties

/opt/kafka/bin/kafka-server-start.sh opt/kafka/config/kraft/server.properties