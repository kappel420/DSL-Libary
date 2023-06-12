def call(Map config = [:], String a) {
    node {
    stage('Fetch Source Code') {
        checkout scm
    }

    stage('Build') {
        sh 'mvn package -DskipTests'
        echo "dupa"
    }

    stage('Run Tests') {
        if (!params.skipTests) {
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


def call(Map config = [:]) {
    node {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            wrap([$class: 'TimestamperBuildWrapper']) {
    stage('Fetch Source Code') {
        checkout scm
    }

    stage('Build') {
        sh 'mvn package -DskipTests'
    }

    stage('Run Tests') {
        if (!params.skipTests) {
            sh 'mvn verify'
            junit '**/target/surefire-reports/*.xml'
        }
    }

    stage('Install Artifact') {
        if (!params.skipInstall) {
            sh 'mvn install -DskipTests'
        }
    }
     cleanWs()
}
}
        }
    }