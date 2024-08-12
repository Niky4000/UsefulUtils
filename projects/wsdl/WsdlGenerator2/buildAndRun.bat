@echo off
call mvn clean package
call docker build -t ru.ibs/WsdlGenerator2 .
call docker rm -f WsdlGenerator2
call docker run -d -p 9080:9080 -p 9443:9443 --name WsdlGenerator2 ru.ibs/WsdlGenerator2