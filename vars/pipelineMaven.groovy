def call(Map config = [skipTests : 1, skipInstall : 1]) {
    node {
    stage('Fetch Source Code') {
        checkout scm
    }

    stage('Build') {
        sh 'mvn package -DskipTests'
    }

    stage('Run Tests') {
        if (!skipTests == 1) {
            sh 'mvn verify'
            junit '**/target/surefire-reports/*.xml'
        }
    }

    stage('Install Artifact') {
        if (!skipInstall == 1) {
            sh 'mvn install -DskipTests'
        }
    }
}
}