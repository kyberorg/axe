pipeline {
    agent any
    stages {
        stage('Init') {
            steps {
                sh 'echo $HOSTNAME'
                sh 'echo "Host (aka VM 0)" >> abc.txt'
            }
        }
        stage('Test') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5.3-jdk-8'
                }
            }
            steps {
                sh 'echo $HOSTNAME'
                sh 'echo "VM 1" >> abc.txt'
            }
        }
        stage('Results') {
            agent {
                docker('maven:3.5.3-jdk-8', reuseNode: true)
            }
            steps {
                sh 'echo $HOSTNAME'
                sh 'echo "VM 2" >> abc.txt'
                //junit(testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true)
            }
        }
        stage('Build') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5.3-jdk-8'
                }
            }
            steps {
                sh 'echo $HOSTNAME'
                sh 'echo "VM 3" >> abc.txt'
                //archive 'target/*.jar'
                archive('abc.txt')
            }
        }
    }
}