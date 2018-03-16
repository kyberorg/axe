pipeline {
  agent {
    docker {
      reuseNode true
      image 'kyberorg/jobbari:1.0'
    }
    
  }
  stages {
    stage('Init') {
      steps {
          sh name: "Executor Info", cmd: '''set +x
echo "Starting building ${PROJECT}"
echo ""
MV=`mvn --version`
DV=`docker --version`

# Internet available ?
wget -q --tries=10 --timeout=20 --spider http://google.com
if [ "$?" -eq "0" ]; then
        NET_STATUS="Host Online"
else
        NET_STATUS="Host Offline"
fi

echo "[Build info]"
echo "Git branch: ${GIT_BRANCH}"
echo "Git commit: ${GIT_COMMIT}"
echo "Jenkins Job #${BUILD_NUMBER}" 
echo "Jenkins Job URL: ${BUILD_URL}"
echo "Jenkins Tag: ${BUILD_TAG}"
echo ""
echo "[Worker info]"
echo "Hostname: ${HOSTNAME}"
echo "Net status: ${NET_STATUS}"
echo ""
echo "Docker version: ${DV}"
echo ""
echo "Maven version: ${MV}"'''
      }
    }
    stage('Test') {
      steps {
        sh 'echo $HOSTNAME'
        sh 'mvn --version'
        sh 'echo "VM 1" >> abc.txt'
      }
    }
    stage('Results') {
      steps {
        sh 'echo $HOSTNAME'
        sh 'echo "VM 2" >> abc.txt'
      }
    }
    stage('Build') {
      steps {
        sh 'echo $HOSTNAME'
        sh 'echo "VM 3" >> abc.txt'
        archive 'abc.txt'
      }
    }
    stage('Create Docker Tag') {
      steps {
        sh 'echo $HOSTNAME'
        sh 'cat abc.txt'
      }
    }
    stage('Create Docker image') {
      steps {
        sh 'echo $HOSTNAME'
        sh 'docker --version'
      }
    }
    stage('Push Docker image') {
      steps {
        sh 'echo $HOSTNAME'
      }
    }
  }
  environment {
    PROJECT = 'Yals'
  }
}