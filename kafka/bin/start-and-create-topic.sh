#!/bin/sh
supervisord -n &

sleep 20s

# create topic
$KAFKA_HOME/bin/kafka-topics.sh --create --topic tweet_conversation_topic \
 --partitions 10 --zookeeper localhost --replication-factor 1 || true

tail -f /dev/null
