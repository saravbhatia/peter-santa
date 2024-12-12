@Library('release') _

String repository = 'ta-call-bot'
boolean enableNewVersioning = true
boolean setNextVersion = true

Closure buildFunction = { ->
    microServiceBuildPipelineGradleInDocker(
        gradleDockerImage: "gradle:8.6-jdk21",
        repository: repository,
        dockerfilesPath: ['ta-call-bot':'./ta-call-bot']
    )
}

Closure stagingDeployFunction = { -> taCDDeployToECSRelease('getFromEnv', 'ta-call-bot', 'ta-pci-dev-fg-microservices') }

Closure stagingSmokeTestFunction = { -> echo 'PLACEHOLDER' }

Closure stagingPrimeDeployFunction = { -> taCDDeployToECSRelease("getFromEnv", "ta-call-bot", "staging-prime-cluster", "us-west-2") }

//Closure prodPDXDeployFunction = { -> taCDDeployToECSRelease("getFromEnv", "ta-call-bot", "prod-microservices", "us-west-2") }

//Closure prodFRADeployFunction = { -> taCDDeployToECSRelease("getFromEnv", "ta-call-bot", "prod-microservices", "eu-central-1") }

Closure productionSmokeTestFunction = { -> echo 'PLACEHOLDER' }

microServicePipelineMain(
    repository: repository,
    buildFunction: buildFunction,
    stagingDeployFunction: stagingDeployFunction,
    stagingPrimeDeployFunction: stagingPrimeDeployFunction,
    //prodPDXDeployFunction: prodPDXDeployFunction,
    //prodFRADeployFunction: prodFRADeployFunction,
    stagingSmokeTestFunction: stagingSmokeTestFunction,
    productionSmokeTestFunction: productionSmokeTestFunction,
    enableNewVersioning: enableNewVersioning,
    setNextVersion: setNextVersion
)
