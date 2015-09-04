#!/bin/bash

projectName=$1
projectNameProperties="$projectName.properties"
debug=$2
origProjectName=oe-system-three-reference
origProjectProperties="pp-service.properties"

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

function updateAllFiles() {

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


if [ "$debug" != "" ]; then
    echo "***** DEBUG MODE ******"
fi

#for file in `ls -d .`
for file in `ls -d */ .`
do
    echo "**** processing directory: $file"

    newName=`echo $file | sed -e "s/${origProjectName}/${projectName}/g"`
    pom=${newName}pom.xml

    moveFile $newName

    updatePom $pom

done

echo "**** replacing properties file names"
updateAllFiles

echo
echo
echo "All done, for more info, check out the hackpad: "
echo "      https://openenglish.hackpad.com/oe-system-reference-reference-madness-qVpdoVWfcVN"
echo
echo "once you have verified your proejct, you can remove all BKP files with this command:"
echo "      find . -name \*.BKP -exec rm {} +"
echo