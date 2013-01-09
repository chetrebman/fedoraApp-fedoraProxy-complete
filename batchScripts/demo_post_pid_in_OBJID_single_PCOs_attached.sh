#!/bin/bash
echo 
echo "NOTE: The user name and password used in this ingest are set fedoraProxy/WEB-INF/applicationContext.xml"
echo

echo "***************************************************"
echo "Did you first run createPid_in_OBJID.sh"
echo
echo "Ready?(y/N)"

read ready <&1
if [ $ready"N" == "N" ]
then
  ready="N"
fi

if [ $ready == "N" -o $ready == "n" ]
then
  exit 
fi


java -jar ./postBatchFile.jar demoUser demoPW  http://localhost:9080/fedoraProxy/du/demo/fedoraAppDemoCollection/ingest.it ./TEST_FILES/post_pid_in_OBJID/batch_pid_in_OBJID.xml  ./TEST_FILES/post_pid_in_OBJID/beaver-at-snow.jpg ./TEST_FILES/post_pid_in_OBJID/beaver-at-snow_access.jpg
