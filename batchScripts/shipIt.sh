#!/bin/sh

echo
echo "This script is used internally to get the distrubution ready for release"
echo
echo "THIS WILL OVERWRITE applicationContext.xml!!!!!"
echo
echo "   Copying  documentation from the javadev/fedoraApp/doc/* to ../doc" 
cp -r ~/javadev/fedoraApp/doc/* ../doc/

echo "   Copying  TEMPLATES/applicationContext.xml TO ../jetty/webapps/fedoraProxy/WEB-INF/applicationContext.xml"

cp TEMPLATES/applicationContext.xml ../jetty/webapps/fedoraProxy/WEB-INF/applicationContext.xml

echo "   Copying  TEMPLATES/webSiteCollection.properites TO ../jetty/webapps/fedoraProxy/WEB-INF/webSiteCollection.properties"

cp TEMPLATES/webSiteCollection.properties ../jetty/webapps/fedoraProxy/WEB-INF/config/webSiteCollection.properties

echo done
rm ../jetty/webapps/fedoraApp/logs/main.log 
rm ../jetty/webapps/fedoraProxy/logs/main.log 
echo


