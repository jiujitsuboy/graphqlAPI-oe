#!/bin/bash

projectName=$1
debug=$2

#
# Validate user input
#
if [ "$projectName" = "" ]; then
    echo "you need to provide the new project prefix, exiting..."
    echo
    echo "Usage:"
    echo "  rename.sh oe-some-new-project-prefix [-d]"
    echo
    exit
fi


#
# Rename directories
#
function moveFile() {
    newName=$1

    if [ "$newName" != "." ]; then
        cmd="git mv $file $newName"
    fi

    if [ "$debug" = "" ]; then
        $cmd
    else
        echo "$cmd"
    fi
}


#
# update the pom file
#
function updatePom() {
    pomFile=$1

    if [ "$pomFile" = ".pom.xml" ]; then
        pomFile="pom.xml"
    fi

    cmd="sed -i 's/pp-service/${projectName}/g' $pomFile"

    if test "$pom"; then
        if [ "$debug" = "" ]; then
            $cmd
        else
            echo "$cmd"
        fi

    fi
}

for file in `ls -d */ .`
do
    newName=`echo $file | sed -e "s/pp-service/${projectName}/g"`
    pom=${newName}pom.xml

    moveFile $newName

    updatePom $pom
done


