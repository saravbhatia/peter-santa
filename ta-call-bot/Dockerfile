FROM 734627308085.dkr.ecr.us-west-2.amazonaws.com/runtime-amazoncorretto21:latest

COPY build/libs/ta-call-bot-*.jar /opt/app.jar

ARG NEXUS_USERNAME
ARG NEXUS_PASSWORD

ENV SERVICE_NAME=ta-call-bot
ARG NEWRELIC_VERSION=8.10.0
ENV NEWRELIC_AGENT="-javaagent:newrelic-agent-$NEWRELIC_VERSION.jar -Dnewrelic.config.file=newrelic.yml -Dnewrelic.config.app_name=$SERVICE_NAME-$DEPLOYMENT_ENVIRONMENT"
RUN curl -s https://repo1.maven.org/maven2/com/newrelic/agent/java/newrelic-agent/$NEWRELIC_VERSION/newrelic-agent-$NEWRELIC_VERSION.jar -o newrelic-agent-$NEWRELIC_VERSION.jar \
    && curl -u $NEXUS_USERNAME:$NEXUS_PASSWORD https://nexus.tripactions.tools/repository/maven-3rdparty/com/tripactions/new-relic-yml/0.2.1/new-relic-yml-0.2.1.zip -o new-relic-yml-0.2.1.zip \
    && yum -y install unzip \
    && unzip new-relic-yml-0.2.1.zip \
    && rm new-relic-yml-0.2.1.zip \
    && chmod +x newrelic-agent-$NEWRELIC_VERSION.jar
ADD start_service.sh /
RUN chmod 755 start_service.sh
ENTRYPOINT ./start_service.sh
