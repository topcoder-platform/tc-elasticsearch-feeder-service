#!/bin/bash
set -e
# Define script variables
DEPLOY_DIR="$( cd "$( dirname "$0" )" && pwd )"
WORKSPACE=$PWD
SERVICE=$1
ENV=$2
TAG_SUFFIX=$3
TAG="$ENV.$TAG_SUFFIX"
IMAGE="tc-elasticsearch-feeder-service:$TAG"
EB_REGION=us-east-1

DOCKERRUN=$DEPLOY_DIR/Dockerrun.aws.json
AWS_S3_KEY="services/elasticbeanstalk/tc-elasticsearch-feeder-service/$IMAGE"

case $ENV in
  "DEV") S3_BUCKET="appirio-platform-dev";;
  "QA") S3_BUCKET="appirio-platform-qa";;
  "PROD") S3_BUCKET="appirio-platform-prod";;
esac

case $ENV in
  "DEV") ENV_NAME=$SERVICE-dev;;
  "QA") ENV_NAME=$SERVICE-qa;;
  "PROD") ENV_NAME=$SERVICE-prod;;
esac

# Elastic Beanstalk Application name
APPNAME=tc-elasticsearch-feeder-service

echo "Deploying to Elasticbeanstalk"
echo "############################"
echo "Update Dockerrun.aws.json file"
sed -i -e "s/%IMAGE%/$IMAGE/g" -e "s/%S3_BUCKET%/$S3_BUCKET/g" $DOCKERRUN

export AWS_ACCESS_KEY_ID=$(eval "echo \$${ENV}_AWS_ACCESS_KEY_ID")
export AWS_SECRET_ACCESS_KEY=$(eval "echo \$${ENV}_AWS_SECRET_ACCESS_KEY")

echo "Pushing Dockerrun.aws.json to s3://${S3_BUCKET}/${AWS_S3_KEY}"
aws s3 cp $DOCKERRUN s3://$S3_BUCKET/$AWS_S3_KEY

echo "Creating new application version $IMAGE in $APPNAME from s3:${S3_BUCKET}/${AWS_S3_KEY}"
aws elasticbeanstalk --region $EB_REGION create-application-version --application-name $APPNAME --version-label $IMAGE --source-bundle S3Bucket="$S3_BUCKET",S3Key="$AWS_S3_KEY"

echo "updating elastic beanstalk environment $ENV_NAME with the version $IMAGE."
# assumes beanstalk app for this service has already been created and configured
aws elasticbeanstalk --region $EB_REGION update-environment --environment-name $ENV_NAME --version-label $IMAGE

