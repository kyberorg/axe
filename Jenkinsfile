pipeline {
    agent any
    stages {
        stage('Init') {
            steps {
                sh 'echo $HOSTNAME'
            }
        }
        withDockerContainer(image: 'maven:3.5.3-jdk-8', toolName: 'Docker') {
            stage('Test') {
                steps {
                    sh 'echo $HOSTNAME'
                }
            }
            stage('Results') {
                steps {
                    sh 'echo $HOSTNAME'
                    //junit(testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true)
                    //archive 'target/*.jar'
                }
            }
            stage('Build') {
                steps {
                    sh 'echo $HOSTNAME'
                }
            }
        }
    }
}