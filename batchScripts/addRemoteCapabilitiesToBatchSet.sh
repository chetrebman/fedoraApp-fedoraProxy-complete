#!/bin/sh

if [ $# -ne 5 ]
then
   echo
   echo "Usage:"
   echo
   echo " topBatch_space_Directory jettyDirectory institution(fedoraNameSpace) batchSetName(collection) collectionPID "
   echo
   exit;
fi

if [ ! -d $1/$3/$4 ];
then
   echo "ERROR: $1/$3/$4 does not exist!"
   exit
fi

if [ ! -d $2 ];
then
   echo "ERROR: $2 does not exist!"
   exit
fi

echo

java -jar ./enableDisableAllRemoteTasksAndIndividualTasks.jar $2 t $3 $4

echo " Install ./TEMPLATES/batchSet_REMOTE.properties to $1/$3/$4/$4_REMOTE.properties"
cp ./TEMPLATES/batchSet_REMOTE.properties $1/$3/$4/$4_REMOTE.properties 
echo

java -jar ./updateRemoteWebsiteCollectionPropertiesFile.jar $2 $3 $4 $5

