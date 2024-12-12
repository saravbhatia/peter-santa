#!/usr/bin/env sh

DEPLOYMENT_ENVIRONMENT=$(echo $SPRING_PROFILES_ACTIVE | cut -d , -f1)
NEWRELIC_AGENT="-javaagent:newrelic-agent-8.10.0.jar -Dnewrelic.config.file=newrelic.yml -Dnewrelic.config.app_name=$SERVICE_NAME-$DEPLOYMENT_ENVIRONMENT"

exec java $APP_JVM_OPTS $HEAP_DUMP_ARGS $JAVA_AGENT_OPTS $NEWRELIC_AGENT -jar /opt/app.jar
