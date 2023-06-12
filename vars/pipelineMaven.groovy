// DSL-Library/src/org/example/PipelineDSL.groovy
def call(Closure body) {
    // Overriding the 'node' step
    node {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            wrap([$class: 'TimestamperBuildWrapper']) {
                try {
                    body()
                } finally {
                    step([$class: 'WsCleanup'])
                }
            }
        }
    }
}

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
                    script {
                        if (!config.skipInstall) {
                            sh 'mvn install -DskipTests'
                        }
                    }
                }
            }
        }
    }
}
