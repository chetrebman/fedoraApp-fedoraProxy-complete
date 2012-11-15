#!/bin/bash

java -jar ./createPid_in_OBJID_BatchFile.jar `pwd`/TEST_FILES/post_pid_in_OBJID/batch_normal_beaver.xml `pwd`/TEST_FILES/post_pid_in_OBJID/batch_pid_in_OBJID.xml localhost 8080 fedoraAdmin fedoraAdmin demo
