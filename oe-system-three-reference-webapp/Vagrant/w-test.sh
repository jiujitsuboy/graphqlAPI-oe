#!/bin/bash

APP_NAME=oe-system-three-reference
LOCAL_PORT=8743
CURL_ARGS="-L -k -i"
echo "==> testing ${APP_NAME} ..."

case "$1" in
    stg)
        MACHINE=${APP_NAME}.stg.openenglish.com
        ;;
    dev)
        MACHINE=${APP_NAME}.dev.openenglish.com:$LOCAL_PORT
        ;;
    *)
        MACHINE=${APP_NAME}.openenglish.com
        ;;
esac

CONTEXT=
echo "==> running health check ping on http..."
curl $CURL_ARGS http://$MACHINE/$CONTEXT/ping
echo
echo "==> running health check ping..."
curl $CURL_ARGS https://$MACHINE/$CONTEXT/ping
echo
echo "==> running extended health check..."
curl $CURL_ARGS https://$MACHINE/$CONTEXT/health




