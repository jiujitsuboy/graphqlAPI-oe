#!/bin/bash
# Grabs and kill a process from the pidlist that has the word myapp

source /etc/default/oe-system-three-reference

pid=`ps aux | grep ${APP_NAME_WEBAPP} | grep -v grep | awk '{print $2}'`
kill -9 $pid
