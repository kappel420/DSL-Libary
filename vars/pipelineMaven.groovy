// DSL-Library/src/org/example/PipelineDSL.groovy

def call(Map config = [:]) {
    pipeline {
        agent {
            label 'tomek'
        }
        stages {
            stage('Fetch Source Code') {
                steps {
                    checkout([$class: 'GitSCM',
                        branches: [[name: config.branch ?: 'main']],
                        userRemoteConfigs: [[url: config.repository ?: '']],
                        extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CloneOption', honorRefspec: false]]])
                }
            }
            stage('Build') {
                steps {
                    sh 'mvn package -DskipTests'
                }
            }
            stage('Run Tests') {
                steps {
                    sh 'mvn verify'
                    junit '**/target/surefire-reports/*.xml'
                }
            }
            stage('Install Artifact') {
                steps {
                    if (!config.skipInstall) {
                        sh 'mvn install -DskipTests'
                    }
                }
            }
        }
    }
}