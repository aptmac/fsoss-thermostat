#!/bin/bash
set -x
TMS_LOC=${PWD##*/}
DOCKER_LOC="/home/jkang/work/docker/tms-storage-docker"

sed s#__tms__#${TMS_LOC}#g ${DOCKER_LOC}/Dockerfile.orig > ../Dockerfile
cp -r ${DOCKER_LOC}/root ../
echo -en "*\n!${TMS_LOC}\n!root" > ../.dockerignore

docker build -t "ts-0-${TMS_LOC}" ../
rm ../Dockerfile
rm ../.dockerignore
rm -r ../root

TIMESTAMP=$(date +%Y-%m-%d-%H-%M-%S)
#docker run --privileged --name=${TMS_LOC}-${TIMESTAMP} "ta-${TMS_LOC}"
