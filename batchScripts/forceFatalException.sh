#!/bin/bash

# T means this is an islandora install, ie objects will assigned to a collection and a content model.
#  specified in a batchIngest_REMOTE.properties file.

islandora="F"

disableHandleServer="T"

enableRemoteIngestTasks="T"

fedoraHost="localhost"
fedoraPort="8080"
fedoraAppFedoraUser="fedoraAdmin"
fedoraAppFedoraPassword="LLLLL"
fedoraProxyFedoraUser="fpUSER"
fedoraProxyFedoraPassword="fpPASSWORD"
worldHandleServer="none"
handleServer="none"
handleServerPort="none"
handleServerApp="none"
solrHost="localhost"
solrPort="8080"
successEmail=
failureEmail=
smtpServer=
smtpPort=
smtpUser=
smtpPassword=

java -jar ./fedoraAppProxyDistInstall.jar $1 $fedoraHost $fedoraPort $fedoraAppFedoraUser $fedoraAppFedoraPassword $fedoraProxyFedoraUser  $fedoraProxyFedoraPassword $islandora $disableHandleServer $worldHandleServer $handleServer $handleServerPort $handleServerApp $solrHost $solrPort $successEmail $failureEmail $smtpServer $smtpUser $smtpPassword $smtpPort $enableRemoteIngestTasks
