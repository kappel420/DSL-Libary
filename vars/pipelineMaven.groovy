def call(Map config = [:]) {
    pipeline {
        agent {
            label 'tomek'
        }
        stages {
            stage('Fetch Source Code') {
                steps {
                    node {
                        checkout([$class: 'GitSCM',
                            branches: [[name: config.branch ?: 'main']],
                            userRemoteConfigs: [[url: config.repository ?: '']],
                            extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CloneOption', honorRefspec: false]]])
                    }
                }
            }
            stage('Build') {
                steps {
                    node {
                        sh 'mvn package -DskipTests'
                    }
                }
            }
            stage('Run Tests') {
                steps {
                    script{
                        node {
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
                    script {
                        node {
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
