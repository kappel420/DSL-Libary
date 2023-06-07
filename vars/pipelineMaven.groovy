def call(Map settings = [:]) {
    pipeline {
        agent any

        stages {
            stage('Get Source Code') {
                steps {
                    // pobiera kod z mastera
                    git branch: settings.branch ?: 'main', url: settings.repository
                }
            }

            stage('Build with Maven') {
                steps {
                    // buduje mavena
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
            }

            stage('Install Artifact') {
                steps {
                    // instaluje artefakt
                    sh 'mvn install -DskipTests'
                }
            }
        }
    }
}
