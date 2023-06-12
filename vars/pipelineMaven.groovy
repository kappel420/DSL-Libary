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


def call(Map config = [:], String a) {
    pipeline {
        agent {
            label 'tomek'
        }
            stage('Fetch Source Code') {
                        checkout([$class: 'GitSCM',
                        branches: [[name: config.branch ?: 'main']],
                        userRemoteConfigs: [[url: config.repository ?: '']],
                        extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CloneOption', honorRefspec: false]]])
            }
            stage('Build') {
                    sh 'mvn package -DskipTests'
            }
            stage('Run Tests') {
                    script{
                        if (!config.skipTests) {
                            sh 'mvn verify'
                            junit '**/target/surefire-reports/*.xml'
                        }
                    }
            
            }
            stage('Install Artifact') {
                    script {
                        if (!config.skipInstall) {
                            sh 'mvn install -DskipTests'
                        }
                    }
                }
            }
    }
    
    wrap([$class: 'AnsiColorBuildWrapper']) {
        wrap([$class: 'TimestamperBuildWrapper']) {
            try {
                body()
            } finally {
                cleanWs()
            }
        }
    }

