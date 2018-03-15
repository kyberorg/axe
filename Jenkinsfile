pipeline {
  agent any
  stages {
    stage('Init') {
      steps {
        sh '''echo "Starting building ${PROJECT}"

echo "[Build info]"
echo "Git branch: ${GIT_BRANCH}"
echo "Git commit: ${GIT_COMMIT}"

# BUILD_NUMBER - 153
# BUILD_URL - http://buildserver/jenkins/job/MyJobName/666/
# BUILD_TAG

echo "[Worker info]"
echo "Hostname: ${HOSTNAME}"
# Uptime
# Internet available ?
# Java Version

'''
      }
    }
    withDockerContainer(image: 'maven:3.5.3-jdk-8', toolName: 'Docker') {
        stage('Test') {
          steps {
            sh 'mvn test -B'
          }
        }
        stage('Report Test Results') {
            steps {
                junit(testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true)
            }
        }
        stage('Build') {
            steps {
                sh 'mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
                archive 'target/*.jar
            }
        }
    } 
  environment {
    PROJECT = 'Yals'
  }
}