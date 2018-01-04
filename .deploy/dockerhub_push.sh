#!/bin/bash
set -e
# Define script variables
DEPLOY_DIR="$( cd "$( dirname "$0" )" && pwd )"
WORKSPACE=$PWD
ENV=$1
TAG_SUFFIX=$2
TAG="$ENV.$TAG_SUFFIX"

DOCKER_REPO=appiriodevops/tc-elasticsearch-feeder-service

cd $DEPLOY_DIR/docker

echo "Copying deployment files to docker folder"
cp $WORKSPACE/target/elasticsearch-feeder-service*.jar elasticsearch-feeder-service.jar
cp $WORKSPACE/src/main/resources/elasticsearch-feeder-service.yaml elasticsearch-feeder-service.yaml

echo "Logging into docker"
echo "############################"
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASSWD

echo "Building docker image $DOCKER_REPO:$TAG"
docker build -t $DOCKER_REPO:$TAG .

echo "Pushing image to docker $DOCKER_REPO:$TAG"
docker push $DOCKER_REPO:$TAG

