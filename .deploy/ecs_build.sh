#!/bin/bash
ENV=$1
if [[ -z "$ENV" ]] ; then
	echo "Environment should be set on startup with one of the below values"
	echo "ENV must be one of - DEV, QA, PROD or LOCAL"
	exit
fi

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
#APP_NAME

OLTP_USER=$(eval "echo \$${ENV}_OLTP_USER")
OLTP_PW=$(eval "echo \$${ENV}_OLTP_PW")
OLTP_URL=$(eval "echo \$${ENV}_OLTP_URL")

JQ="jq --raw-output --exit-status"


# Define script variables
DEPLOY_DIR="$( cd "$( dirname "$0" )" && pwd )"
WORKSPACE=$PWD


cd $DEPLOY_DIR/docker


echo "Copying deployment files to docker folder"
cp $WORKSPACE/target/elasticsearch-feeder-service*.jar elasticsearch-feeder-service.jar
cp $WORKSPACE/src/main/resources/elasticsearch-feeder-service.yaml elasticsearch-feeder-service.yaml

echo "Logging into docker"
echo "############################"
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASSWD

#Converting environment varibale as lower case for build purpose
#ENV=`echo "$ENV" | tr '[:upper:]' '[:lower:]'`
#echo "$ENV after case conversion"

configure_aws_cli() {
	aws --version
	aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
	aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
	aws configure set default.region $AWS_REGION
	aws configure set default.output json
	echo "Configured AWS CLI."
}

build_ecr_image() {
	eval $(aws ecr get-login  --region $AWS_REGION)
	# Builds Docker image of the app.
	#$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$CIRCLE_SHA1
	TAG=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$CIRCLE_SHA1
	docker build -t $TAG .
}

push_ecr_image() {	
	echo "Pushing Docker Image...."
	eval $(aws ecr get-login --region $AWS_REGION --no-include-email)
	echo $TAG
	docker push $TAG
	echo "Docker Image published."
}

register_definition() {
    if revision=$(aws ecs register-task-definition --container-definitions "$task_def" --family $family | $JQ '.taskDefinition.taskDefinitionArn'); then
        echo "Revision: $revision"
    else
        echo "Failed to register task definition"
        return 1
    fi

}


check_service_status() {
        counter=0
	sleep 60
        servicestatus=`aws ecs describe-services --service $AWS_ECS_SERVICE --cluster $AWS_ECS_CLUSTER | $JQ '.services[].events[0].message'`
        while [[ $servicestatus != *"steady state"* ]]
        do
           echo "Current event message : $servicestatus"
           echo "Waiting for 30 sec to check the service status...."
           sleep 30
           servicestatus=`aws ecs describe-services --service $AWS_ECS_SERVICE --cluster $AWS_ECS_CLUSTER | $JQ '.services[].events[0].message'`
           counter=`expr $counter + 1`
           if [[ $counter -gt $COUNTER_LIMIT ]] ; then
                echo "Service does not reach steady state with in 10 minutes. Please check"
                exit 1
           fi
        done
        echo "$servicestatus"
}

make_task_def(){
task_template=$(cat <<-END
{
  "executionRoleArn": "arn:aws:iam::811668436784:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    { 
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/tc-elasticsearch-feeder-service",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },     
      "portMappings": [
        {
          "hostPort": 8080,
          "protocol": "tcp",
          "containerPort": 8080
        },
        {
          "hostPort": 8081,
          "protocol": "tcp",
          "containerPort": 8081
        }
      ],      
      "cpu": 1,
      "environment": [
        {
          "name": "AUTH_DOMAIN",
          "value": "%s"
        },
        {
          "name": "AWS_SIGNING_ENABLED",
          "value": "%s"
        },
        {
          "name": "CHALLENGES_INDEX_NAME",
          "value": "%s"
        },
        {
          "name": "ELASTIC_SEARCH_URL",
          "value": "%s"
        },
        {
          "name": "OLTP_PW",
          "value": "%s"
        },
        {
          "name": "OLTP_URL",
          "value": "%s"
        },
        {
          "name": "OLTP_USER",
          "value": "%s"
        },
        {
          "name": "TC_JWT_KEY",
          "value": "%s"
        }
      ],      
      "memoryReservation": 512,      
      "image": "%s",      
      "essential": true,      
      "name": "tc-elasticsearch-feeder-service"
    }
  ],
  "memory": "4096",
  "taskRoleArn": "arn:aws:iam::811668436784:role/ecsTaskExecutionRole",  
  "family": "tc-elasticsearch-feeder-service",  
  "requiresCompatibilities": [
    "FARGATE"
  ],
  "networkMode": "awsvpc",
  "cpu": "2048"
}
END
)
    
  task_def=$(printf "$task_template" "$AUTH_DOMAIN" $AWS_SIGNING_ENABLED $CHALLENGES_INDEX_NAME $ELASTIC_SEARCH_URL $OLTP_PW "$OLTP_URL" $OLTP_USER $TC_JWT_KEY $TAG)  
  echo $task_def
  echo $task_def > config.json
}

register_definition() {
    echo "register definition"
    echo aws ecs register-task-definition --cli-input-json file://config.json --family $family
    if revision=$(aws ecs register-task-definition  --cli-input-json file://config.json --family $family | $JQ '.taskDefinition.taskDefinitionArn'); then
        echo "Revision: $revision"
    else
        echo "Failed to register task definition"
        return 1
    fi
}

deploy_cluster(){
  echo "Revision"
    echo $revision
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
