#!/bin/bash
set -x
TMS_LOC=${PWD##*/}
DOCKER_LOC="/home/jkang/work/docker/tms-agent-docker"

sed s#__tms__#${TMS_LOC}#g ${DOCKER_LOC}/Dockerfile.orig > ../Dockerfile
cp -r ${DOCKER_LOC}/root ../
cp -r ${DOCKER_LOC}/thermostat-user-home-config ../
echo -en "*\n!${TMS_LOC}\n!root\n!thermostat-user-home-config" > ../.dockerignore

docker build -t "ta-0-${TMS_LOC}" ../
rm ../Dockerfile
rm ../.dockerignore
rm -r ../root
rm -r ../thermostat-user-home-config

TIMESTAMP=$(date +%Y-%m-%d-%H-%M-%S)
#docker run --privileged --name=${TMS_LOC}-${TIMESTAMP} "ta-${TMS_LOC}"
