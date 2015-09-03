#!/bin/bash
# Grabs and kill a process from the pidlist that has the word myapp

pid=`ps aux | grep pp-service-webapp | grep -v grep | awk '{print $2}'`

if [ "$pid" != "" ]; then
    echo "PP_SERVICE Server is running."
else
    echo "PP_SERVICE Server is NOT running."
fi
