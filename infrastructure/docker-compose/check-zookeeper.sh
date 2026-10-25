#!/usr/bin/env bash

response=$(echo ruok | nc -w 2 localhost 2181)

if [ "$response" = "imok" ]; then
    echo "ZooKeeper is healthy"
    exit 0
else
    echo "ZooKeeper is unhealthy. Response: '$response'"
    exit 1
fi