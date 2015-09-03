#!/bin/bash

BASE_DIR=`dirname $0`

source /etc/default/pp-service

cd $BASE_DIR

java $JAVA_OPTS -jar $PP_SERVICE_HOME_DIR/app/pp-service-webapp.jar > $PP_SERVICE_BOOT_LOG 2>&1 &
