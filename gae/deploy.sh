#!/bin/bash
set -e

SDK_PATH="/usr/local/appengine-java-sdk-1.3.1"
JAR_PATH=$SDK_PATH"/lib/user/appengine-api-1.0-sdk-1.3.1.jar"
WAR_PATH="gae/war"

mkdir -p $WAR_PATH/WEB-INF
rm -rf $WAR_PATH/WEB-INF/lib $WAR_PATH/WEB-INF/classes
cp -r lib $WAR_PATH/WEB-INF/lib
cp $JAR_PATH $WAR_PATH/WEB-INF/lib
cp -r classes $WAR_PATH/WEB-INF/classes

$SDK_PATH/bin/appcfg.sh update $WAR_PATH
