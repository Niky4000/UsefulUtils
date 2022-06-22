#!/bin/sh
mvn clean package && docker build -t ru.ibs/WsdlGenerator .
docker rm -f WsdlGenerator || true && docker run -d -p 9080:9080 -p 9443:9443 --name WsdlGenerator ru.ibs/WsdlGenerator