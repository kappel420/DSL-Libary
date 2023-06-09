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
                        branches: [[name: config.branch ?: 'master']],
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
                    script {
                        try {
                            if (!settings.skipTests) {
                                sh 'mvn verify'
                                junit 'target/surefire-reports/*.xml'
                            }
                        } catch (Exception e) {
                            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/surefire-reports/*.xml'
                }
            }
            }
            stage('Install Artifact') {
                steps {
                    script {
                        if (!settings.skipInstall) {
                            sh 'mvn install -DskipTests'
                }
            }
        }
    }
}
}
}
}
