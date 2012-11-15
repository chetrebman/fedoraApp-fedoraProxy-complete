#!/bin/sh

if [ $# -ne 3 ]
then
   echo
   echo "Usage:"
   echo
   echo " topDirectory institution(fedoraNameSpace) batchSetName(typically a collection) "
   echo
   exit;
fi

if [ ! -d $1/$2/$3 ];
then
   echo "ERROR: $1/$2/$3 does not exist!"
   exit
fi

echo

java -jar ./enableDisableAllRemoteTasksAndIndividualTasks.jar $1 t $2 $3

cp ./TEMPLATES/batchSet_TASK.properties $1/$2/$3/$3_TASK.properties 

echo
echo " YOU MUST CREATE A TASK CLASS FILE AND ADD IT TO  $1/$2/$3/$3_TASK.properties"
echo
