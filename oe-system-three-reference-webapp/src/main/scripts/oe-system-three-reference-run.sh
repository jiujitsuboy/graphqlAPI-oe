#!/bin/bash

BASE_DIR=`dirname $0`

source /etc/default/oe-system-three-reference

cd $BASE_DIR

java $JAVA_OPTS -jar ${APP_HOME_DIR}/app/${APP_NAME_WEBAPP}.jar > ${APP_BOOT_FILE} 2>&1 &
