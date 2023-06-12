def call(Map config = [:]) {
    node {
    stage('Fetch Source Code') {
        checkout scm
    }

    stage('Build') {
        sh 'mvn package -DskipTests'
    }

    stage('Run Tests') {
        if (params.skipTests) {
            sh 'mvn verify'
            junit '**/target/surefire-reports/*.xml'
        }
    }

    stage('Install Artifact') {
        if (!params.skipInstall) {
            sh 'mvn install -DskipTests'
        }
    }
}
}