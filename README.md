# fsoss-thermostat
Thermostat Presentation @ FSOSS 2016

<b> Setting up Byteman & Running the demo </b>
1. export BYTEMAN_HOME={..PATH TO BYTEMAN..}/byteman-3.6.0
2. export PATH=${PATH}:${BYTEMAN_HOME}/bin
3. java -javaagent=${BYTEMAN_HOME}/lib/byteman.jar=script:example-rule.btm Hello world
