#!/bin/bash

projectName=$1

#
# Validate user input
#
if [ "$projectName" = "" ]; then
    echo "you need to provide the new project prefix, exiting..."
    echo
    echo "Usage:"
    echo "  rename.sh oe-some-new-project-prefix"
    echo
    exit
fi


#
# Rename directories
#
function moveFile() {
    newName=$1

    if [ "$newName" != "." ]; then
        echo "git move $file $newName"
    fi
}


#
# update the pom file
#
function updatePom() {
    pomFile=$1

    if test "$pom"; then
        echo "  sed -i -e 's/pp-service/${projectName}/g'" $pomFile
    fi
}

for file in `ls -d */ .`
do
    newName=`echo $file | sed -e "s/pp-service/${projectName}/g"`
    pom=${newName}pom.xml

    moveFile $newName

    updatePom $pom
done


