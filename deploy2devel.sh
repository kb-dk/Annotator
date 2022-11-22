#!/usr/bin/env bash
set -e

echo 'Building war file'
mvn -DskipTests clean package
echo 'Deploying war to cop-devel-01.kb.dk'
scp target/annotation*.war cop@cop-devel-01:services/webapps/annotation.war
echo 'Done'
