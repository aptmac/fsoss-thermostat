#!/bin/bash

for var in MONGO_URL MONGO_USERNAME MONGO_PASSWORD THERMOSTAT_CLIENT_USERNAME THERMOSTAT_CLIENT_PASSWORD THERMOSTAT_AGENT_USERNAME THERMOSTAT_AGENT_PASSWORD
do

exit=0
echo "${!var}"
if [ -z "${!var}" ]; then
  echo "The ${var} environment variable is required"
  exit=1
fi
done

if [ "${exit}" -eq "1" ]; then
  exit 1
fi

