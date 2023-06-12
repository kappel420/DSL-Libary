def call(Map config = [:]) {
    node {
    stage('Fetch Source Code') {
        checkout([$class: 'GitSCM',
            branches: [[name: params.branch ?: 'main']],
            userRemoteConfigs: [[url: params.repository ?: '']],
            extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CloneOption', honorRefspec: false]]
        ])
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
}
}