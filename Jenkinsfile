pipeline {
  agent none
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
      agent {
        docker {
          image 'maven:3.5.3-jdk-8'
        }
        
      }
      steps {
        sh 'mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
      }
    }
    stage('Test') {
      agent {
        docker {
          image 'maven:3.5.3-jdk-8'
        }
        
      }
      steps {
        sh 'mvn test -B'
      }
    }
    stage('Report Test Results') {
      parallel {
        stage('Report Test Results') {
          agent {
            docker {
              image 'maven:3.5.3-jdk-8'
            }
            
          }
          steps {
            junit(testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true)
          }
        }
        stage('Collecting Info') {
          steps {
            sh '''echo "[Adding SCM info to Docker build]"

echo "${GIT_COMMIT}" > COMMIT

export VERY_LATEST_COMMIT=`git describe --tags $(git rev-list --tags --max-count=1)`

echo $VERY_LATEST_COMMIT
'''
            waitUntil() {
              fileExists 'COMMIT'
            }
            
          }
        }
      }
    }
    stage('Build Docker image') {
      steps {
        sh '''echo "[Adding SCM info to Docker build]"

echo "${GIT_COMMIT}" > COMMIT
export VERY_LATEST_COMMIT=`git describe --tags $(git rev-list --tags --max-count=1)`
export LATEST_COMMIT_IN_BRANCH=`git describe --tags --abbrev=0`

echo "Verbose info. Commit $TRAVIS_COMMIT, Very last tag (all branches) $VERY_LATEST_COMMIT, Last tag (in current branch) $LATEST_COMMIT_IN_BRANCH"

export TAG=`if [ "$G_BRANCH" == "master" ]; then echo $LATEST_COMMIT_IN_BRANCH; else echo $VERY_LATEST_COMMIT; fi`
echo $TAG > TAG

cat COMMIT
cat TAG
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