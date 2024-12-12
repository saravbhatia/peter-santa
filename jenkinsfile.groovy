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

Closure stagingPrimeDeployFunction = { -> echo 'PLACEHOLDER' }

Closure prodPDXDeployFunction = { ->  echo 'PLACEHOLDER' }

Closure prodFRADeployFunction = { -> echo 'PLACEHOLDER' }

Closure productionSmokeTestFunction = { -> echo 'PLACEHOLDER' }

microServicePipelineMain(
    repository: repository,
    buildFunction: buildFunction,
    stagingDeployFunction: stagingDeployFunction,
    stagingPrimeDeployFunction: stagingPrimeDeployFunction,
    prodPDXDeployFunction: prodPDXDeployFunction,
    prodFRADeployFunction: prodFRADeployFunction,
    stagingSmokeTestFunction: stagingSmokeTestFunction,
    productionSmokeTestFunction: productionSmokeTestFunction,
    enableNewVersioning: enableNewVersioning,
    setNextVersion: setNextVersion
)
