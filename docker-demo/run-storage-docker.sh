#!/bin/bash

# argument : description : example
# $1 : name of container : storage
# $2 : mongodb url : mongodb://172.17.0.2:27017
# $3 : mongodb user : mongo
# $4 : mongodb password : mongo

AGENT_USER=agent
AGENT_PWD=agent
CLIENT_USER=client
CLIENT_PWD=client

CONTAINER_NAME=tms-hg-web-storage

docker run  -t \
            --name ${CONTAINER_NAME} \
            -e THERMOSTAT_AGENT_USERNAME=${AGENT_USER} \
            -e THERMOSTAT_AGENT_PASSWORD=${AGENT_PWD} \
            -e THERMOSTAT_CLIENT_USERNAME=${CLIENT_USER} \
            -e THERMOSTAT_CLIENT_PASSWORD=${CLIENT_PWD} \
            tms-hg-web-storage
