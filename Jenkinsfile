pipeline {
  agent {
    docker {
      reuseNode true
      image 'kyberorg/jobbari:1.2.3'
      args '-u root --privileged'
    }
    
  }
  stages {
    stage('Init') {
      steps {
        sh '''##### Gathering info ####



set +x
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
        sh '''##### Preparing git info #####

set +x
git checkout ${GIT_BRANCH}
git pull --tags
export VERY_LATEST_COMMIT=$(git describe --tags $(git rev-list --tags --max-count=1))
export LATEST_COMMIT_IN_BRANCH=`git describe --tags --abbrev=0`
echo "Verbose info. Commit ${GIT_COMMIT}, Very last tag (all branches) ${VERY_LATEST_COMMIT}, Last tag (in current branch) ${LATEST_COMMIT_IN_BRANCH}"
export TAG=`test "${GIT_BRANCH}" = "master"; then echo $LATEST_COMMIT_IN_BRANCH; else echo $VERY_LATEST_COMMIT; fi`
echo ${GIT_COMMIT} > COMMIT
echo $TAG > TAG

git checkout -f ${GIT_COMMIT}'''
        sh 'id'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test -B'
      }
    }
    stage('Results') {
      steps {
        junit(testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true)
      }
    }
    stage('Build') {
      steps {
        sh 'mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
        archive 'target/*.jar'
        sh 'chmod ugo+w -R .'
      }
    }
    stage('Create Docker Tag') {
      steps {
        sh '''set +x
case ${GIT_BRANCH} in
      master) DOCKER_TAG="stable" ;;
      trunk) DOCKER_TAG="latest" ;;
          *) DOCKER_TAG="${GIT_BRANCH}-latest" ;;
esac

echo "Docker Tag: ${DOCKER_TAG}"
echo ${DOCKER_TAG} > DOCKER_TAG
'''
      }
    }
    stage('Create Docker image') {
      steps {
        sh '''set +x 
service docker start
DOCKER_TAG=`cat DOCKER_TAG`
echo "Building Docker image with: $DOCKER_TAG"
docker build -t $DOCKER_REPO:$DOCKER_TAG .
'''
      }
    }
    stage('Push Docker image') {
      steps {
        sh '''set +x
echo "Logging in to Docker hub as $DOCKER_HUB_USR"
docker login -u $DOCKER_HUB_USR -p $DOCKER_HUB_PSW
echo "Pushing image to $DOCKER_REPO"
docker push $DOCKER_REPO
'''

      }
    }
  }
  environment {
    PROJECT = 'Yals'
    DOCKER_REPO = 'yals/kyberorg'
    DOCKER_USER = 'kyberorg'
    DOCKER_HUB = credentials('docker-hub')
  }
}