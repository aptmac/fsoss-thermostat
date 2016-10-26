#!/bin/bash

# argument : description : example 
# $1 : name of container : agent
# $2 : storage url : http://172.17.0.3:8999/thermostat/storage
# $3 : agent username : agent
# $4 : agent password : agent

AGENT_CMDC_ADDR=127.0.0.1
AGENT_CMDC_PORT=12000
AGENT_USER=agent
AGENT_PWD=agent


CONTAINER_NAME=tms-hg-agent
STORAGE_IP=$(sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' tms-hg-web-storage)
STORAGE_PORT=8999
DB_URL=http://${STORAGE_IP}:${STORAGE_PORT}/thermostat/storage

if [ "$#" -eq 2 ]; then
  AGENT_USER=$3
  AGENT_PWD=$4
fi

docker run  -t --privileged --pid host --net host \
            --name ${CONTAINER_NAME} \
            -e THERMOSTAT_DB_URL=${DB_URL} \
            -e THERMOSTAT_AGENT_USERNAME=${AGENT_USER} \
            -e THERMOSTAT_AGENT_PASSWORD=${AGENT_PWD} \
            -e THERMOSTAT_CMDC_PORT=${AGENT_CMDC_PORT} \
            -e THERMOSTAT_CMDC_ADDR=${AGENT_CMDC_ADDR} \
            -v /docker/tmp:/tmp \
            tms-hg-agent
