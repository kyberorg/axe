@Library('common-lib@1.7') _

//global vars
def buildProfile = 'dev';

def dockerTag = 'dev';
def dockerTags = [];

def deployNamespace = 'dev-yals';
def deployWorkloadName = 'yals-app';
def deployContainerName = 'app';

def testEnabled = true;
def testUrl = "https://dev.yals.eu";

pipeline {
  agent any;
  environment {
    DOCKER_REPO = 'yadev/yals'
  }
  parameters {
    string(name: 'DOCKER_TAG', defaultValue: "", description: 'Custom Docker image Tag')
    booleanParam(name: 'REVIEW', defaultValue: false, description: 'Do code review: code-style report')
    booleanParam(name: 'PRODUCTION_BUILD', defaultValue: false, description: 'Deploy to Production')
    booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Do NOT run Tests')
  }

  stages {
    stage('Vaadin') {
      steps {
        script {
          vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting', runSiteTarget: params.REVIEW)
        }
      }
    }

    stage('Auto Code Review') {
      when {
        expression {
          return params.REVIEW
        }
      }
      steps {
        script {
          publishHTML([
                  allowMissing         : true,
                  alwaysLinkToLastBuild: false,
                  keepAll              : true,
                  reportDir            : 'target/site',
                  reportFiles          : 'checkstyle.html',
                  reportName           : 'HTML Report',
                  reportTitles         : ''
          ])

        }
      }
    }

    stage('Setting Build Params') {
      parallel {
        stage('Dev') {
          when {
            not {
              anyOf {
                branch 'trunk'
                buildingTag()
                expression {
                  return params.PRODUCTION_BUILD
                }
              }
            }
          }
          steps {
            script {
              buildProfile = 'dev';
            }
            script {
              def customDockerTag = params.DOCKER_TAG;
              if (!customDockerTag.trim().equals("")) {
                dockerTag = customDockerTag;
              } else {
                dockerTag = 'dev';
              }
              dockerTags << dockerTag;
            }
            script {
              deployNamespace = 'dev-yals';
              deployWorkloadName = 'yals-app';
            }
            script {
              testEnabled = !params.SKIP_TESTS;
              testUrl = "https://dev.yals.eu";
            }

          }
        }
        stage('QA/Demo Build') {
          when {
            branch 'trunk'
          }
          steps {
            script {
              buildProfile = 'qa';
            }
            script {
              def customDockerTag = params.DOCKER_TAG;
              if (!customDockerTag.trim().equals("")) {
                dockerTag = customDockerTag;
              } else {
                dockerTag = 'dev';
              }
              dockerTags << dockerTag;
            }
            script {
              deployNamespace = 'qa-yals';
              deployWorkloadName = 'yals-app';
            }
            script {
              testEnabled = !params.SKIP_TESTS;
              testUrl = "https://qa.yals.eu";
            }

          }
        }
        stage('PROD Build') {
          when {
            anyOf {
              buildingTag()
              expression {
                return params.PRODUCTION_BUILD
              }
            }
          }
          steps {
            script {
              buildProfile = 'PROD';
            }
            script {
              def customDockerTag = params.DOCKER_TAG;
              if (!customDockerTag.trim().equals("")) {
                dockerTag = customDockerTag;
              } else {
                dockerTag = 'dev';
              }
              dockerTags << dockerTag;
            }
            script {
              deployNamespace = 'prod-yals';
              deployWorkloadName = 'yals-app';
            }
            script {
              testEnabled = false;
              testUrl = "https://yals.eu";
            }

          }
        }

      }
    }

    stage('Docker') {
      steps {
        script {
          dockerBuild(repo: env.DOCKER_REPO, tags: dockerTags);
          dockerLogin(creds: 'hub-docker');
          dockerPush();
          dockerLogout();
          dockerClean();
        }
      }
    }

    stage('Deploy') {
      steps {
        script {
          print 'Deploying to ' + buildProfile;
          deployToKube(
                  namespace: deployNamespace,
                  workloadName: deployWorkloadName,
                  imageRepo: env.DOCKER_REPO,
                  imageTag: dockerTag,
                  containerName: deployContainerName
          )
        }
      }
    }

    stage("Wait For Deploy prior Testing") {
      when {
        expression {
          return testEnabled
        }
      }
      options {
        timeout(time: 2, unit: 'MINUTES')
      }
      input {
        message: 'Should we continue?'
      }
      steps {
        echo 'Waiting for deployment to complete prior starting smoke testing'
      }
    }
    stage('App and UI Tests') {
      when {
        expression {
          return testEnabled
        }
      }
      steps {
        script {
          def buildName = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}";
          withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: 'hub-creds',
                            usernameVariable: 'USR', passwordVariable: 'PASS'
                           ]]) {
            testApp(url: testUrl, dParams: "-Dcom.vaadin.testbench.Parameters.hubHostname=grid.yatech.eu " +
                    '-Dtest.browsers=chrome ' +
                    "-Dtest.buildName=${buildName} " +
                    '-Dtest=!eu.yals.test.ui.pages.**',
                    actions: 'clean test',
                    artifacts: "target/*.png", failStep: false);
          }
        }
      }
    }
  }
  post {
    always {
      chuckNorris();
      cleanWs();
    }
  }
}
