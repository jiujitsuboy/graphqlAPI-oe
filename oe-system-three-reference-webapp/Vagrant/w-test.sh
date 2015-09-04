#!/bin/bash

echo "==> testing pp-service..."
CURL_ARGS="-L -k -i"

case "$1" in
    stg)
        MACHINE=pp-service.stg.openenglish.com
        ;;
    dev)
        MACHINE=pp-service.dev.openenglish.com:8743
        ;;
    *)
        MACHINE=pp-service.openenglish.com
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




