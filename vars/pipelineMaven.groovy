def call(Map config = [:], Closure body) {
    def script = CpsScript.current()
    script.node {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            wrap([$class: 'TimestamperBuildWrapper']) {
                try {
                    body()
                } finally {
                    script.cleanWs()
                }
            }
        }
    }
}

def call(Map config = [:], Closure body) {
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
