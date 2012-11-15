#!/bin/bash

if [ $# -ne 3 ]
then
   echo
   echo "Usage:"
   echo
   echo "  topDir institution(fedoraNameSpace) batchSetName "
   echo
   exit;
fi

if [ ! -d $1/$2 ]; 
then
    echo
    echo "ERROR $1/$2 does not exist!"    
    echo
    exit
fi

if [ ! -d $1/$2/$3 ]; 
then
    echo
    echo "ERROR $1/$2/$3 does not exist!"   
    echo
    exit
fi

echo
echo "Delete logs? y/N"
read deleteLogs <&1
if [ $deleteLogs"N" == "N" ]
then
  deleteLogs="N"
fi

echo "cleaning directories"

rm ../batch_space/$1/$2/work/*                >& /dev/null
rm ../batch_space/$1/$2/files/*               >& /dev/null
rm ../batch_space/$1/$2/mets/new/*            >& /dev/null
rm ../batch_space/$1/$2/mets/updates/*        >& /dev/null
rm ../batch_space/$1/$2/tasksTemp/*           >& /dev/null
rm ../batch_space/$1/$2/completed/*           >& /dev/null
rm ../batch_space/$1/$2/completedBatchFiles/* >& /dev/null
rm ../batch_space/$1/$2/failed/*              >& /dev/null
rm ../batch_space/$1/$2/failedBatchFiles/*    >& /dev/null

if [ $deleteLogs == "Y" -o $deleteLogs == "y" ]
then
  echo "Deleting logs"
  rm ../batch_space/$1/$2/logs/* >& /dev/null
else
  echo "Keeping logs"
fi

echo "done!"


