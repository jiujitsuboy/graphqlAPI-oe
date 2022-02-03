#!/bin/bash

source /etc/default/oe-hr-portal-service

# Grabs and kill a process from the pidlist that has the word myapp

pid=`ps aux | grep ${APP_NAME_WEBAPP} | grep -v grep | awk '{print $2}'`

if [ "$pid" != "" ]; then
    echo "${APP_NAME} Server is running."
else
    echo "${APP_NAME} Server is NOT running."
fi
