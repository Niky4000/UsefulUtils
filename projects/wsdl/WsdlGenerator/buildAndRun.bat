@echo off
call mvn clean package
call docker build -t ru.ibs/WsdlGenerator .
call docker rm -f WsdlGenerator
call docker run -d -p 9080:9080 -p 9443:9443 --name WsdlGenerator ru.ibs/WsdlGenerator