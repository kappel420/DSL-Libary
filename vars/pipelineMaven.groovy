def customNode(Closure body) {
    node {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            wrap([$class: 'TimestamperBuildWrapper']) {
                try {
                    body()
                } finally {
                    cleanWs()
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
                    if (!config.skipInstall) {
                    sh 'mvn verify'
                    junit '**/target/surefire-reports/*.xml'
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
