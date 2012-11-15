#!/bin/bash

# T means this is an islandora install, ie objects will assigned to a collection and a content model.
#  specified in a batchIngest_REMOTE.properties file.

islandora="T"
islandoraCollectionName="demoCollection"
islandoraCollectionPid="SET_ME"

disableHandleServer="f"

enableRemoteIngestTasks="F"

#NOTE use XXXXX="none" to show an unset value

topLevelDirectory=`pwd`/..
batchSpaceDirectory=`pwd`/../batch_space

fedoraHost="localhost1"
fedoraPort="80802"
fedoraAppFedoraUser="fedoraAdmin3"
fedoraAppFedoraPassword="fedoraAdmin4"
fedoraProxyFedoraUser="fedoraAdmin5"
fedoraProxyFedoraPassword="fedoraAdmin6"
fedoraNamespace="demo7"

worldHandleServer="8"
handleServer="9"
handleServerPort="10"
handleServerApp="11"

solrHost="localhost12"
solrPort="8080_13"

successEmail="chet.rebman@du.edu_14"
failureEmail="chet.rebman@du.edu_15"
fromEmail=fedoraApp@adr.org_16
smtpServer="smtp.indra.com_17"
smtpPort="465_18"
smtpUser="aSmptpUser"
smtpPassword="aSmtpPassword"
smtpSSL="f"


java -jar ./fedoraAppProxyDistInstall.jar $topLevelDirectory $batchSpaceDirectory $fedoraHost $fedoraPort $fedoraAppFedoraUser $fedoraAppFedoraPassword $fedoraProxyFedoraUser $fedoraProxyFedoraPassword $islandora $disableHandleServer $worldHandleServer $handleServer $handleServerPort $handleServerApp $solrHost $solrPort $successEmail $failureEmail $smtpServer $smtpPort $smtpUser $smtpPassword $enableRemoteIngestTasks $fedoraNamespace $fromEmail $smtpSSL  $islandoraCollectionName $islandoraCollectionPid

chmod +x getPids.sh
chmod +x createPid_in_OBJID_BatchFile.sh
