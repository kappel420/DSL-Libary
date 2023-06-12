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
                    customNode {
                        checkout([$class: 'GitSCM',
                            branches: [[name: config.branch ?: 'main']],
                            userRemoteConfigs: [[url: config.repository ?: '']],
                            extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CloneOption', honorRefspec: false]]])
                    }
                }
            }
            stage('Build') {
                steps {
                    customNode {
                        sh 'mvn package -DskipTests'
                    }
                }
            }
            stage('Run Tests') {
                steps {
                    customNode {
                        script {
                            if (!config.skipTests) {
                        sh 'mvn verify'
                        junit '**/target/surefire-reports/*.xml'
                    }
                }
            }
            }
            }
            stage('Install Artifact') {
                steps {
                    customNode {
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
}
