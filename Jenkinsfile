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
    stage('Build') {
      steps {
        sh 'mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test -B'
      }
    }
    stage('Report Test Results') {
      steps {
        junit 'target/surefire-report/**/*.xml'
      }
    }
    stage('Build Docker image') {
      steps {
        sh '''echo "[Adding SCM info to Docker build]"
'''
        sh '''echo "[Preparing Docker Tag]"
'''
        sh '''echo "[Building Docker image]"
'''
      }
    }
    stage('Release to DockerHub') {
      steps {
        sh '''echo "[Releasing image to DockerHub]"
'''
      }
    }
    stage('Apply change to DEV') {
      steps {
        sleep 5
        sh '''echo "[Deploying artifact to DEV]"
# connect
# go to directory
# docker pull
# docker-compose up -d'''
      }
    }
  }
  environment {
    PROJECT = 'Yals'
  }
}