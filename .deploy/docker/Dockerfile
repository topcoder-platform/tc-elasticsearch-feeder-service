FROM appiriodevops/ap-microservice-base:0.0.1

WORKDIR /data

COPY elasticsearch-feeder-service.jar /data/elasticsearch-feeder-service.jar

COPY elasticsearch-feeder-service.yaml /data/elasticsearch-feeder-service.yaml

COPY newrelic.yml /usr/local/share/newrelic/newrelic.yml

CMD java -jar /data/elasticsearch-feeder-service.jar server /data/elasticsearch-feeder-service.yaml

EXPOSE 8080 8081
