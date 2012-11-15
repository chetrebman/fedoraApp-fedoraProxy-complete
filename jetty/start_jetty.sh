
##cat /dev/null > nohup.out

##nohup java -Dlog4j.debug -jar start.jar etc/jetty-plus.xml  etc/jetty.xml &

java -Dlog4j.debug -jar start.jar etc/jetty-plus.xml  etc/jetty.xml &
