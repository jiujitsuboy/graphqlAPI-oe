#!/bin/bash

sudo service pp-service stop
sudo service activemq stop
sudo dpkg --purge pp-service-webapp
sudo rm -fr /opt/open-english/pp-service
sudo sh /opt/open-english/install/oe-active-mq/uninstall-oe-activemq.sh
sudo rm -fr /opt/open-english/install/oe-active-mq

echo
echo "Enviornment cleaned up..."
echo "refresh global config and build your env locally, I'll wait"
read -n1 -r -p " Press a key to continue..." key
echo
sudo sh /usr/local/sbin/oe-refresh-global-config.sh

sudo dpkg -i /home/vagrant/target/pp-service-webapp_0.0~SNAPSHOT_all.deb

echo 
echo  -----------------------------
echo 
echo  "check on ActiveMQ --> http://localhost:9561/admin/"
echo
echo  -----------------------------


PP_SERVICE_LOG=/opt/open-english/pp-service/logs/pp-service.log
echo  "Waiting for $PP_SERVICE_LOG log file to be created by the application."
while [ ! -f $PP_SERVICE_LOG ]
do
  sleep 2
done
sudo tail -f $PP_SERVICE_LOG



