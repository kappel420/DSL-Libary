def call(Map settings = [:]) {
    node {
        agent any

        stages {
            stage('Get Source Code') {
                    // pobiera kod z mastera
                    //git branch: settings.branch ?: 'main', url: settings.repository
                    checkout ([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs:[[url: "https://github.com/kappel420/spring-petclinic"]]])
            }

            stage('Build with Maven') {
                    // buduje mavena
                    sh 'mvn package -DskipTests'
                
            }

            stage('Run Tests') {
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
                    // instaluje artefakt
                    sh 'mvn install -DskipTests'
            }
        }
    }
}
