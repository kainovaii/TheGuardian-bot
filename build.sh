#!/bin/bash

echo ">>> Build Maven"
mvn clean package

if [ $? -ne 0 ]; then
    echo "Build failed."
    exit 1
fi

echo ">>> Running JAR"

clear

java -jar target/Guardian-1.0-jar-with-dependencies.jar