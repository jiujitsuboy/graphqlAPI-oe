#!/bin/sh

#
# These steps are handled by the deploy process
# https://openenglish.jira.com/wiki/display/ARCH/OE+Deploy+Process
#
# add OE config info to system, the only part needed by the deb installer is 'env dev'
mkdir -p /opt/open-english/setup
cat <<EOF >/opt/open-english/setup/CONFIGDATA
groupId com.openenglish.pp
artifactId pp-service
version FIXED-SNAPSHOT
env dev
EOF

#
# Here we are recreating what the deploy process and golden-ami do for us in stg/prod
#
DEV_ENV_DIR=/opt/open-english/setup/dev
mkdir -p $DEV_ENV_DIR
cp /home/vagrant/user-home/pp-service.properties $DEV_ENV_DIR/pp-service.properties

#
# copy the SSL files to the correct place in the VM, accessed by apache
#
SSL_DIR=$DEV_ENV_DIR/ssl
mkdir -p $SSL_DIR
cp /vagrant/pp-service.crt $SSL_DIR
cp /vagrant/pp-service.key $SSL_DIR

# fetch global properties from github
cat <<EOF >/usr/local/sbin/oe-refresh-global-config.sh
DEV_ENV_DIR=/opt/open-english/setup/dev
USER_PASSWORD="oe-vagrant:elaine1"
HEADER="Accept: application/vnd.github.VERSION.raw"
URL="https://api.github.com/repos/openenglish/pp-global-config/contents/src/main/resources/pp-global-config.properties"
OUTPUT="\${DEV_ENV_DIR}/pp-global-config.properties"
curl -u "\${USER_PASSWORD}" -H "\${HEADER}" "\${URL}" -o "\${OUTPUT}"
EOF

# run the script
sh /usr/local/sbin/oe-refresh-global-config.sh

#
# wget global config from github
# curl -u gabipetrovay -H "Accept: application/vnd.github.raw" "https://api.github.com/repos/user/repo/contents/filename"
# create script to fetch updated global config from github
#

# update the vagrant box /etc/hosts to look at the host machine for pp-service.
# At some point, pp-service will be in a vagrant box too, so this will need to
# be updated.
ETC_HOSTS=/etc/hosts
HOST_OS_IP=10.0.2.2
LOCAL_HOST_IP=127.0.0.1
POSTGRES_NAME="postgres.dev.openenglish.com"
HOSTS_APP_NAME="pp-service.dev.openenglish.com"

if ! grep -q "$POSTGRES_NAME" "$ETC_HOSTS"; then
    # update the VM /etc/hosts file
    echo "$HOST_OS_IP  $POSTGRES_NAME" >> $ETC_HOSTS
fi

if ! grep -q "$HOSTS_APP_NAME" "$ETC_HOSTS"; then
    echo "$LOCAL_HOST_IP $HOSTS_APP_NAME" >> $ETC_HOSTS
fi