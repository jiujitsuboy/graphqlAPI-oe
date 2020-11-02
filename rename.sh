#!/bin/bash

projectName=$1
projectNameProperties="$projectName.properties"
projectShortName=$2
projectPortPrefix=$3
debug=$4
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
else
    echo "Project name: $1"
    echo "Project short name for logback: $2"
    echo "Project port number prefix (e.g. 88): $3"
    echo "Debug mode: $4"
    echo
    echo "Confirm if this is correct. Do you want to continue? [Y|n]"
    read confirm

    if [ "$confirm" = "n" -o "$confirm" = "N" ]; then
        exit
    fi
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
            cp $file $file.BKP; cat $file.BKP | sed "s/${origProjectName}/${projectName}/g" > $file
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
    vminitFile=`find . -name vminit\*`
    madnessFile=`find . -name mad\*`
    wtestFile=`find . -name w-tes\*`
    postinstFile=`find . -name postinst`
    runFile=`find . -name \*run.sh`
    statusFile=`find . -name \*status.sh`
    stopFile=`find . -name \*stop.sh`

    if [ "$debug" = "" ]; then
	    cp $vagrantFile $vagrantFile.BKP; cat $vagrantFile.BKP | sed "s/${origName}/${newName}/g" > $vagrantFile
	    cp $vagrantFile $vagrantFile.BKP; cat $vagrantFile.BKP | sed "s/${origPort}/${projectPortPrefix}/g" > $vagrantFile
	    cp $madnessFile $madnessFile.BKP; cat $madnessFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $madnessFile
	    cp $wtestFile $wtestFile.BKP; cat $wtestFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $wtestFile
	    cp $wtestFile $wtestFile.BKP; cat $wtestFile.BKP | sed "s/${origPort}/${projectPortPrefix}/g" > $wtestFile
	    cp $postinstFile $postinstFile.BKP; cat $postinstFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $postinstFile
	    cp $vminitFile $vminitFile.BKP; cat $vminitFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $vminitFile
	    cp $runFile $runFile.BKP; cat $runFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $runFile
	    cp $statusFile $statusFile.BKP; cat $statusFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $statusFile
	    cp $stopFile $stopFile.BKP; cat $stopFile.BKP | sed "s/${origProjectName}/${projectName}/g" > $stopFile
    else
        echo "updating Vagrantfile"
	    echo "updating madness.sh"
	    echo "updating other system specific files"
    fi
}
function cleanup() {
    find . -name \*.BKP -exec rm {} +
}

function createLocalProperties() {

    userProperties=~/${projectName}.properties
    tryAgain=true
    if ! [ -e ${userProperties} ]; then
        while $tryAgain; do
            echo "Please enter the name of a file to base ${userProperties} on:"
            read sourceFile

            homeSourceFile=~/$sourceFile
            homeSourceFileProperties=~/$sourceFile.properties

            if [ -e $sourceFile ]; then
                cp $sourceFile $userProperties
            elif [ -e $homeSourceFile ]; then
                cp $homeSourceFile $userProperties
            elif [ -e $homeSourceFileProperties ]; then
                cp $homeSourceFileProperties $userProperties
            else
                echo "I could not find $sourceFile, I also checked ($homeSourceFile and $homeSourceFileProperties)"
                echo "would you like to enter the name again? [y|n]"
                read doItAgain

                if [ "$doItAgain" = "y" -o "$doItAgain" = "Y" ]; then
                    tryAgain=true
                    continue
                fi
            fi
            # set tryAgain to false by default, only if we have an issue will we loop again
            tryAgain=false
        done
    fi
}

function gitReset() {

    echo "process git updates, includes commit and changing remote repo? [y|n]"
    read updateGit

    updateGit=`echo $updateGit | awk '{print toupper($0)}'`

    if [ "$updateGit" = "Y" ]; then
        # commit all of our changes
        echo "committing all changes"
        git commit -a -m "system-three app gen, initial commit"

        echo "enter remote git URL"
        read remoteGitUrl

        echo "updating origin..."
        git remote rm origin
        git remote add origin ${remoteGitUrl}

        echo "remote origin added, to push your changes to the remote repo use:"
        echo
        echo "git push -u origin master"
    fi

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

echo "**** creating local properties"
createLocalProperties

echo "**** cleaning up BK files"
cleanup

echo "**** git updates"
gitReset

echo
echo
echo "All done: "
echo "    * make sure you added ${projectName}.dev.openenglish.com to your /etc/hosts file"
echo " "
echo "          127.0.0.1       ${projectName}.dev.openenglish.com"
echo " "
echo " "
echo "    * for more info, check out the hackpad: "
echo "      https://openenglish.hackpad.com/oe-system-reference-reference-madness-qVpdoVWfcVN"
echo
echo
