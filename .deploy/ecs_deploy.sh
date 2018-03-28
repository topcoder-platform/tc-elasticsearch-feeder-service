#!/bin/bash
ENV=$1
if [[ -z "$ENV" ]] ; then
	echo "Environment should be set on startup with one of the below values"
	echo "ENV must be one of - DEV, QA, PROD or LOCAL"
	exit
fi
#Compute ENV variables from CC
echo "$ENV before case conversion"

AWS_REGION=$(eval "echo \$${ENV}_AWS_REGION")
AWS_ACCESS_KEY_ID=$(eval "echo \$${ENV}_AWS_ACCESS_KEY_ID")
AWS_SECRET_ACCESS_KEY=$(eval "echo \$${ENV}_AWS_SECRET_ACCESS_KEY")
AWS_ACCOUNT_ID=$(eval "echo \$${ENV}_AWS_ACCOUNT_ID")
AWS_REPOSITORY=$(eval "echo \$${ENV}_AWS_REPOSITORY")
AWS_ECS_CLUSTER=$(eval "echo \$${ENV}_AWS_ECS_CLUSTER")
AWS_ECS_SERVICE=$(eval "echo \$${ENV}_AWS_ECS_SERVICE")
family=$(eval "echo \$${ENV}_AWS_ECS_TASK_FAMILY")
AWS_ECS_CONTAINER_NAME=$(eval "echo \$${ENV}_AWS_ECS_CONTAINER_NAME")
AUTH_DOMAIN=$(eval "echo \$${ENV}_AUTH_DOMAIN")
AWS_SIGNING_ENABLED=$(eval "echo \$${ENV}_AWS_SIGNING_ENABLED")
ELASTIC_SEARCH_URL=$(eval "echo \$${ENV}_ELASTIC_SEARCH_URL")
TC_JWT_KEY=$(eval "echo \$${ENV}_TC_JWT_KEY")
REDISSON_JOB_SINGLE_SERVER_ADDRESS=$(eval "echo \$${ENV}_REDISSON_JOB_SINGLE_SERVER_ADDRESS")
LOG_LEVEL=$(eval "echo \$${ENV}_LOG_LEVEL")
CHALLENGES_INDEX_NAME=$(eval "echo \$${ENV}_CHALLENGES_INDEX_NAME")
#APP_NAME
OLTP_USER=$(eval "echo \$${ENV}_OLTP_USER")
OLTP_PW=$(eval "echo \$${ENV}_OLTP_PW")
OLTP_URL=$(eval "echo \$${ENV}_OLTP_URL")

JQ="jq --raw-output --exit-status"


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
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASSWD

#Converting environment varibale as lower case for build purpose
#ENV=`echo "$ENV" | tr '[:upper:]' '[:lower:]'`
#echo "$ENV after case conversion"

configure_aws_cli() {
  echo "Configuring AWS CLI."
	aws --version
	aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
	aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
	aws configure set default.region $AWS_REGION
	aws configure set default.output json
	echo "Configured AWS CLI."
}

build_ecr_image() {
  echo "Building docker image..."
 	eval $(aws ecr get-login  --region $AWS_REGION)
	TAG=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$CIRCLE_SHA1
	docker build -t $TAG .
  echo "Docker image built with the TAG :"
  echo $TAG

}

push_ecr_image() {	
  echo "Pushing docker image to ECR..."
 	eval $(aws ecr get-login --region $AWS_REGION --no-include-email)
	echo $TAG
	docker push $TAG
 echo "Docker image published to ECR"
}


make_task_def(){
  echo "Creating ECS task definition..."  
  task_template=`cat ecs_task_template.json`
  task_def=$(printf "$task_template" $AWS_ACCOUNT_ID $AWS_ECS_SERVICE $AWS_REGION "$AUTH_DOMAIN" $AWS_SIGNING_ENABLED $CHALLENGES_INDEX_NAME $ELASTIC_SEARCH_URL $OLTP_PW "$OLTP_URL" $OLTP_USER $TC_JWT_KEY $REDISSON_JOB_SINGLE_SERVER_ADDRESS $LOG_LEVEL $TAG $AWS_ECS_SERVICE $AWS_ACCOUNT_ID $AWS_ECS_SERVICE)
  echo $task_def > task_def.json
  echo "ECS task definition is created : "
  echo $task_def
}

register_definition() {
    echo "Registering  ECS task definition..."
    echo aws ecs register-task-definition --cli-input-json file://task_def.json --family $family
    if revision=$(aws ecs register-task-definition  --cli-input-json file://task_def.json --family $family | $JQ '.taskDefinition.taskDefinitionArn'); then
        echo "Revision: $revision"
    else
        echo "Failed to register task definition"
        return 1
    fi
}

deploy_cluster(){
    echo "Revision"
    echo $revision
    echo "Updating ECS Service..."
    make_task_def
    register_definition
    update_result=$(aws ecs update-service --cluster $AWS_ECS_CLUSTER --service $AWS_ECS_SERVICE --task-definition $revision )
    result=$(echo $update_result | $JQ '.service.taskDefinition' )
    echo $result
    if [[ $result != $revision ]]; then
        echo "Error updating service."
        return 1
    fi

    echo "Update service intialised successfully for deployment"
    return 0
}

configure_aws_cli
build_ecr_image
push_ecr_image
deploy_cluster
#check_service_status
exit $?
