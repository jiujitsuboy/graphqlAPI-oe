#!/bin/bash

BASE_DIR=`dirname $0`

source /etc/default/oe-hr-portal-service

cd $BASE_DIR

java $JAVA_OPTS -jar ${APP_HOME_DIR}/app/${APP_NAME_WEBAPP}-exec.jar > ${APP_BOOT_FILE} 2>&1 &
