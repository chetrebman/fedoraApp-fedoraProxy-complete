#!/bin/bash

if [ $# -ne 3 ]
then
echo -e "\nUSAGE: the {path to top of batch_space} {institution} {batchSet} \n"
exit
fi

mkdir $1/$2
mkdir $1/$2/$3

mkdir $1/$2/$3/completed
mkdir $1/$2/$3/completedBatchFiles
mkdir $1/$2/$3/failed
mkdir $1/$2/$3/failedBatchFiles
mkdir $1/$2/$3/files
mkdir $1/$2/$3/images
mkdir $1/$2/$3/logs
mkdir $1/$2/$3/mets
mkdir $1/$2/$3/mets/new
mkdir $1/$2/$3/mets/updates
mkdir $1/$2/$3/tasksTemp
mkdir $1/$2/$3/work
##mkdir $1/$2/$3/mixed

