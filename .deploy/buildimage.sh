#!/bin/bash

# fail if any occurs
set -e

ENV=$1
# Define script variables
DEPLOY_DIR="$( cd "$( dirname "$0" )" && pwd )"
WORKSPACE=$PWD
cd $DEPLOY_DIR/docker
echo "Copying deployment  files to docker folder"
cp $WORKSPACE/target/elasticsearch-feeder-service*.jar elasticsearch-feeder-service.jar
cp $WORKSPACE/src/main/resources/elasticsearch-feeder-service.yaml elasticsearch-feeder-service.yaml
cp $WORKSPACE/.deploy/ecs_task_template.json ecs_task_template.json

echo "Logging into docker"
echo "############################"
#docker login $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASSWD
#docker login -u $DOCKER_USER -p $DOCKER_PASSWD
aws s3 cp "s3://appirio-platform-$ENV/services/common/dockercfg" ~/.dockercfg
#TAG=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$CIRCLE_SHA1
TAG="elasticsearchfeeder:latest"
docker build -t $TAG .
