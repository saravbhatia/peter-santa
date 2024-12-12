# ta-call-bot-gradle

TODO: Add description of your service.

TODO: add your PRD/design doc here.

### Installation:
```shell
/.gradlew build
```

### Running the app:

#### From Intellij
Use the run SpringBoot application

#### From the shell
```shell
gradle -Dspring.profiles.active=local bootRun
```

Call ```localhost:8080/api/taCallBot/helloWorld``` to make sure the service is up and running.

### Jenkins job
TODO: add the jenkins job for master repository.

### Code Structure
* **ta-call-bot-server**: This module contains the business related code

* **ta-call-bot-client**: This module contains the public API document and client for this service

* **ta-call-bot**: This module contains the docker file and main app. It is the launch point of the
  Spring Application. All the SRE related scripts will be in this module as well.

### Documentation
For documentation - update the Swagger yaml - ta-call-bot.yaml

**Not implemented yet** - plugin for auto generation of yaml - https://github.com/swagger-api/swaggerhub-gradle-plugin

Internal Doc - https://navancorp.atlassian.net/wiki/spaces/REL/pages/3053519293/SwaggerHub+Integration.
When using current config, swaggerhubUpload job in jenkins file will upload the swagger yaml for master branch CI CD.

Swagger url: https://app.swaggerhub.com/apis/TripActions/TaCallBotAPI/1.0.1