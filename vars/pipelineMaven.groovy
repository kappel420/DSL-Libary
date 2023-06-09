// DSL-Library/src/org/example/PipelineDSL.groovy

def call(Map config) {
    def repository = config.repository
    def branch = config.branch
    def skipTests = config.skipTests ?: false
    def skipInstall = config.skipInstall ?: false

    pipeline {
        agent none
        stages {
            stage('Example') {
                agent any
                steps {
                    script {
                        echo "test"
                    }
                    script {
                        pipelineMavenStep(repository, branch, skipTests, skipInstall)
                    }
                }
            }
        }
    }
}

def pipelineMavenStep(repository, branch, skipTests, skipInstall) {
    stage('Fetch Source Code') {
        // Code to fetch source code from the repository
    }

    stage('Build') {
        // Code to build the project using 'mvn package -DskipTests'
    }

    stage('Run Tests') {
        // Code to run tests using 'mvn verify' and import JUnit results
    }

    stage('Install Artifact') {
        // Code to install artifact in local repository using 'mvn install -DskipTests'
    }
}
