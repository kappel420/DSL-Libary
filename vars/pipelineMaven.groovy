def myNode(Closure body) {
  node {
    ansiColor('xterm') {
      wrap([$class: 'TimestamperBuildWrapper']) {
        body.call()
      }
    }
  }
}

def call(Map config = [:], Closure body) {
  pipeline {
    agent {
      label 'tomek'
    }
    stages {
      myNode {
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
            script {
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
  post {
    always {
      cleanWs()
    }
  }
}
