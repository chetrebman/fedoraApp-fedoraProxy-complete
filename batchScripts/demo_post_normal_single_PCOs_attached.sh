#!/bin/bash

echo 
echo "NOTE: The user name and password used in this ingest are set in fedoraProxy/WEB-INF/applicationContext.xml"
echo
#
java -jar ./postBatchFile.jar demoUser demoPW  http://localhost:9080/fedoraProxy/du/demo/fedoraAppDemoCollection/ingest.it ./TEST_FILES/post_normal_single/batch_normal_single.xml ./TEST_FILES/post_normal_single/a-bear-in-the-bushes-ursus-americanus.jpg ./TEST_FILES/post_normal_single/a-bear-in-the-bushes-ursus-americanus_access.jpg
#
echo "Done"
echo
