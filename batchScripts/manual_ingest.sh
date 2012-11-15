echo "#!/bin/bash"
echo
cp  TEST_FILES/manual_ingest/batch_normal.xml ../batch_space/demo/fedoraAppDemoCollection/work/
cp TEST_FILES/manual_ingest/*.jpg ../batch_space/demo/fedoraAppDemoCollection/files/
#
echo "**********************************************************************"
echo "   USING...institution=demo and batchSet=fedoraAppDemoCollection" 
echo "**********************************************************************"
echo
echo "  Make sure jetty is running, use the 'start_jetty.cmd' in the jetty home directory"
echo
echo "  Take your web browser to http://localhost:9080/fedoraApp/"
echo
echo "  Log into Fedora"
echo
echo "  Click on the 'Fedora batch ingest' link"
echo
echo "  Select institution 'demo' and batchSet 'fedoraAppDemoCollection'"
echo
echo "  Set the checkbox to allow simultaneus Manual and Remote ingests (If this is a new batch tree, this checkbox will not be displayed) "
echo
echo "  Set the checkbox to 'Split work' file"
echo 
echo "  From the dropdown select the work file 'batch_normal.xml"
echo
echo "  Press the 'Ingest All' button"
echo
echo
