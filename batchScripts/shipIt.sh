#!/bin/sh

echo
echo "This script is used internally to get the distrubution ready for release"

echo "   Coping  TEMPLATES/applicationContext.xml TO ../jetty/webapps/fedoraProxy/WEB-INF/applicationContext.xml"

cp TEMPLATES/applicationContext.xml ../jetty/webapps/fedoraProxy/WEB-INF/applicationContext.xml

echo "   Coping  TEMPLATES/webSiteCollection.properites TO ../jetty/webapps/fedoraProxy/WEB-INF/webSiteCollection.properties"

cp TEMPLATES/webSiteCollection.properties ../jetty/webapps/fedoraProxy/WEB-INF/config/webSiteCollection.properties

echo done
rm ../jetty/webapps/fedoraApp/logs/main.log 
rm ../jetty/webapps/fedoraProxy/logs/main.log 
echo


