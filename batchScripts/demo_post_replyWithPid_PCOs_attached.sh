#!/bin/bash
echo
echo "NOTE: The user name and password used in this ingest are set fedoraProxy/WEB-INF/applicationContext.xml"
echo

java -jar ./postBatchFile.jar demoUser demoPW  http://localhost:9080/fedoraProxy/du/demo/fedoraAppDemoCollection/ingest.it ./TEST_FILES/post_replyWithPid/batch_replyWithPid.xml ./TEST_FILES/post_replyWithPid/a-black-bear-and-cub-travel.jpg ./TEST_FILES/post_replyWithPid/a-black-bear-and-cub-travel_access.jpg
