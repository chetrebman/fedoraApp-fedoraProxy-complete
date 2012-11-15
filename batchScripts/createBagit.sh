#!/bin/bash

java -jar ./bagitCreator.jar `pwd`/TEST_FILES/bagit http://localhost:9080/bagit/ 

## rm ../jetty/webapps/bagit/data/*

cp ./TEST_FILES/bagit/data/* ../jetty/webapps/bagit/data/



