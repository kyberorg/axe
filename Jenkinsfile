pipeline {
    agent any
    stages {
        stage('Init') {
            steps {
                sh 'echo $HOSTNAME'
            }
        }
        stage('Test') {
            agent {
                docker('maven:3.5.3-jdk-8')
            }
            steps {
                sh 'echo $HOSTNAME'
            }
        }
        stage('Results') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5.3-jdk-8'
                }
            }
            steps {
                sh 'echo $HOSTNAME'
                //junit(testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true)
                //archive 'target/*.jar'
            }
        }
        stage('Build') {
            agent {
                docker('maven:3.5.3-jdk-8')
            }
            steps {
                sh 'echo $HOSTNAME'
            }
        }
    }
}