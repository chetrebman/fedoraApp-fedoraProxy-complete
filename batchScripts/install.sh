#!/bin/bash

topLevelDirectory=`pwd`/..
batchSpaceDirectory=`pwd`/../batch_space

#NOTE use XXXXX="none" to show an unset value

# T means this is an islandora install, ie objects will assigned to a collection and a content model.
#  specified in a batchIngest_REMOTE.properties file. 

islandora="f"

fedoraNamespace="demo"
batchSetName="fedoraAppDemoCollection"

# NOTE: Enabling OF REMOTE ingest can be turned ON/OFF with enableDisableAllRemoteTasks.sh

enableRemoteIngestTasks="f"

fedoraHost="localhost"
fedoraPort="8080"
fedoraAppFedoraUser="fedoraAdmin"
fedoraAppFedoraPassword="fedoraAdmin"
fedoraProxyFedoraUser="fedoraAdmin"
fedoraProxyFedoraPassword="fedoraAdmin"

# For Email to work all of these must be set, other wise make them ="NOT_SET" NOTE: to disable in a properties file, you would put nothing after the = 
successEmail="chet.rebman@du.edu"
failureEmail="chet.rebman@du.edu"
fromEmail=fedoraApp@adr.org
smtpServer="smtp.indra.com"
smtpPort="465"
smtpUser="NOT_SET"
smtpPassword="NOT_SET"
smtpSSL="T"

# SOLR CAN BE ACCESSED THROUGH fedoraProxy AND HAS NOTHING TO DO WITH INGEST
solrHost="localhost"
solrPort="8080"

# LEAVE THESE ALONE UNLESS YOU HAVE HANDLE SERVERS SETUP
disableHandleServer="T"
worldHandleServer="none"
handleServer="none"
handleServerPort="none"
handleServerApp="none"

if [ ! -d ../batch_space ]; 
then
   mkdir ../batch_space
fi

if [ ! -d ../batch_space/$fedoraNamespace/$batchSetName ]; 
then
   echo
   echo "*********************************************************************************************************************************"
   echo "Creating a batch directory tree ../batch_space/$fedoraNamespace to match the  fedora namespace this is also refered to as the "
   echo "  'institution' for a multi-instituion repository and will appear in the GUI dropdown menu."
   echo "*********************************************************************************************************************************"

   echo "./make_batch_tree.sh '../batch_space/' $fedoraNamespace $batchSetName"
      ./make_batch_tree.sh '../batch_space/' $fedoraNamespace $batchSetName
   echo
else
   echo
   echo "Using existing  batch tree->../batch_space/$fedoraNamespace/$batchSetName"
   echo
fi


java -jar ./fedoraAppProxyDistInstall.jar $topLevelDirectory $batchSpaceDirectory $fedoraHost $fedoraPort $fedoraAppFedoraUser $fedoraAppFedoraPassword $fedoraProxyFedoraUser $fedoraProxyFedoraPassword $islandora $disableHandleServer $worldHandleServer $handleServer $handleServerPort $handleServerApp $solrHost $solrPort $successEmail $failureEmail $smtpServer $smtpPort $smtpUser $smtpPassword $enableRemoteIngestTasks $fedoraNamespace $fromEmail $smtpSSL  $batchSetName 

cat > manual_ingest.sh <<EOT
echo "#!/bin/bash"
echo
cp  TEST_FILES/manual_ingest/batch_normal.xml ../batch_space/$fedoraNamespace/$batchSetName/work/
cp TEST_FILES/manual_ingest/*.jpg ../batch_space/$fedoraNamespace/$batchSetName/files/
#
echo "**********************************************************************"
echo "   USING...institution=$fedoraNamespace and batchSet=$batchSetName" 
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
echo "  Select institution '$fedoraNamespace' and batchSet '$batchSetName'"
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
EOT

cat > demo_post_normal_single_PCOs_attached_mixed.sh <<EOT
echo "#!/bin/bash"
echo
cp TEST_FILES/mixed_ingest/batch_normal_mixed.xml ../batch_space/$fedoraNamespace/mixed/work/
cp TEST_FILES/mixed_ingest/*.jpg                  ../batch_space/$fedoraNamespace/mixed/files/
#
echo "**********************************************************************"
echo "   USING...institution=$fedoraNamespace and batchSet=mixed"
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
echo "  Select institution '$fedoraNamespace' and batchSet 'mixed'"
echo
echo "  Set the checkbox to allow simultaneus Manual and Remote ingests (If this is a new batch tree, this checkbox will not be displayed) "
echo
echo "  Set the checkbox to 'Split work' file"
echo 
echo "  From the dropdown select the work file 'batch_normal_mixed.xml"
echo
echo "  Press the 'Ingest All' button"
echo
echo
EOT

chmod +x getPids.sh
chmod +x createPid_in_OBJID_BatchFile.sh
chmod +x manual_ingest.sh
