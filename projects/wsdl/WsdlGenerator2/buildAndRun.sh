#!/bin/sh
mvn clean package && docker build -t ru.ibs/WsdlGenerator2 .
docker rm -f WsdlGenerator2 || true && docker run -d -p 9080:9080 -p 9443:9443 --name WsdlGenerator2 ru.ibs/WsdlGenerator2