#!/bin/bash

SERVICE_NAME=oe-hr-portal-service
SERVICE_WEBAPP_NAME=$SERVICE_NAME-webapp

sudo service $SERVICE_NAME stop
sudo dpkg --purge $SERVICE_WEBAPP_NAME
sudo rm -fr /opt/open-english/$SERVICE_NAME

echo
echo "Enviornment cleaned up..."
echo "refresh global config and build your env locally, I'll wait"
read -n1 -r -p " Press a key to continue..." key
echo
# -- uncomment this if you want payments global config to be refreshed with every run of madness.sh
sudo sh /usr/local/sbin/oe-refresh-global-config.sh

sudo dpkg -i "/home/vagrant/target/${SERVICE_WEBAPP_NAME}_0.0~SNAPSHOT_all.deb"


SERVICE_LOG="/opt/open-english/$SERVICE_NAME/logs/${SERVICE_NAME}.log"
echo  "Waiting for $SERVICE_LOG log file to be created by the application."
while [ ! -f $SERVICE_LOG ]
do
  sleep 2
done
sudo tail -f $SERVICE_LOG



