#!/bin/bash

projectName=$1
projectNameProperties="$projectName.properties"
projectShortName=$2
projectPortPrefix=$3
debug=$3
origProjectName=oe-system-three-reference
projectShortShortName="SERVICE"
origProjectProperties="oe-system-three-reference.properties"

if [ "$#" -lt 3 ]; then
    echo "Wrong number of arguments."
    echo
    echo "Usage:"
    echo "  rename.sh your-project short-name port-number [-d]"
    echo
    echo "  your-project -> the name of your project, e.g. oe-sync"
    echo "  short-name   -> project name for logback.xml, e.g. SYNC"
    echo "  port-number  -> port number PREFIX for your Vagrant instance, e.g. 88"
    echo
    echo "See the hackpad (Vagrant Ports): https://openenglish.hackpad.com/Vagrant-Ports-EYFdItONvzT"
    echo
    exit
fi

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
    origName=$1
    newName=$2

    if [ "$newName" != "." ]; then
        cmd="git mv $origName $newName"
    fi

    if [ "$debug" = "" ]; then
        echo $cmd
        $cmd
    else
        echo $cmd
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

    cmd="mv $pomFile $pomFile.BKP; cat $pomFile.BKP | sed 's/${origProjectName}/${projectName}/g' > $pomFile"

    if [ "$debug" = "" ]; then
        echo $cmd
        cp $pomFile $pomFile.BKP; cat $pomFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $pomFile
    else
        echo $cmd
    fi

}

function updateJavaFiles() {

    for file in `find . -name \*.java`
    do
        echo "processing file: $file"

        cmd="cp $file $file.BKP; cat $file.BKP | sed 's/${origProjectProperties}/${projectNameProperties}/g' > $file"

        if [ "$debug" = "" ]; then
            echo $cmd
            cp $file $file.BKP; cat $file.BKP | sed "s/${origProjectProperties}/${projectNameProperties}/g" > $file
        else
            echo $cmd
        fi

    done
}

function updateLogbackFiles() {

    for file in `find . -name logback.xml`
    do
        echo "processing file: $file"

        cmd="cp $file $file.BKP; cat $file.BKP | sed 's/${projectShortShortName}/${projectShortName}/g' > $file"

        if [ "$debug" = "" ]; then
            echo $cmd
            cp $file $file.BKP; cat $file.BKP | sed "s/${projectShortShortName}/${projectShortName}/g" > $file
        else
            echo $cmd
        fi

    done
}

function updateProjectSpecificFiles() {
    for file in `find . -name \*oe-system-three-reference\*`
    do
        echo "processing file: $file"

        newName=`echo $file | sed -e "s/${origProjectName}/${projectName}/g"`
        moveFile $file $newName

    done
}

function vagrantUpdates() {
    origName=oe_system_three_reference
    origPort=87
    newName=`echo $projectName | sed -e "s/-/_/g"`
    vagrantFile=`find . -name Vagrantfile`

    cp $vagrantFile $vagrantFile.BKP; cat $vagrantFile.BKP | sed "s/${origName}/${newName}/g" > $vagrantFile
    cp $vagrantFile $vagrantFile.BKP; cat $vagrantFile.BKP | sed "s/${origPort}/${projectPortPrefix}/g" > $vagrantFile
}

function cleanup() {
    find . -name \*.BKP -exec rm {} +
}

if [ "$debug" != "" ]; then
    echo "***** DEBUG MODE ******"
fi

# rename all directories....
for file in `ls -d */ .`
do
    echo "**** processing directory: $file"

    newName=`echo $file | sed -e "s/${origProjectName}/${projectName}/g"`
    pom=${newName}pom.xml

    moveFile $file $newName

    updatePom $pom

done

echo "**** replacing properties file names"
updateJavaFiles

echo "**** updating logback.xml files"
updateLogbackFiles

echo "**** setting up project specific files"
updateProjectSpecificFiles

echo "**** updating Vagrant file"
vagrantUpdates

echo "**** cleaning up BK files"
cleanup

echo
echo
echo "All done, for more info, check out the hackpad: "
echo "      https://openenglish.hackpad.com/oe-system-reference-reference-madness-qVpdoVWfcVN"
echo
echo