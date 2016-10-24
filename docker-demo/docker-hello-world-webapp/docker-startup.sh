#!/bin/bash

"java" "-jar" "hello-world-webapp-0.1-SNAPSHOT-jar-with-dependencies.jar" 2>&1 > server.log &

tail -f server.log
