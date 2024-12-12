@Library('release') _

String repository = 'baseline-be-svc'
boolean enableNewVersioning = true
boolean setNextVersion = true

Closure buildFunction = { ->
    microServiceBuildPipelineGradleInDocker(
        gradleDockerImage: "gradle:8.6-jdk21",
        repository: repository,
        dockerfilesPath: ['baseline-be-svc':'./baseline-be-svc']
    )
}

Closure stagingDeployFunction = { -> taCDDeployToECSRelease('getFromEnv', 'baseline-be-svc', 'ta-pci-dev-fg-microservices') }

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
