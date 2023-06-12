def call(Map config = [:]) {
    wrap([$class: 'TimestamperBuildWrapper']) {
        wrap([$class: 'AnsiColorBuildWrapper']) {
                try {
                    pipelineBody(config)
                } finally {
                    cleanWs()
                }
        }
    }
}

def pipelineBody(Map config) {
    // Your existing pipeline code here
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
                    script{
                        if (!config.skipTests) {
                            sh 'mvn verify'
                            junit '**/target/surefire-reports/*.xml'
                        }
                    }
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
