@Library('common-lib@1.7') _

//global vars
def dockerTag = 'dev'

pipeline {
  agent any;
  environment {
    DOCKER_REPO = 'yadev/yals'
  }
  parameters {
    booleanParam(name: 'REVIEW', defaultValue: false, description: 'Do code review: code-style report')
    string(name: 'DOCKER_TAG', defaultValue: "", description: 'Custom Docker image Tag')
    booleanParam(name: 'PRODUCTION_BUILD', defaultValue: false, description: 'Deploy to Production')
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
    stage('Docker (Dev Build)') {
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
          def customDockerTag = params.DOCKER_TAG;
          def tags = [];
          if (!customDockerTag.trim().equals("")) {
            dockerTag = customDockerTag;
          } else {
            dockerTag = 'dev'
          }
          tags << dockerTag;

          dockerBuild(repo: env.DOCKER_REPO, tags: tags);
          dockerLogin(creds: 'hub-docker');
          dockerPush();
          dockerLogout();
          dockerClean();
        }
      }
    }
    stage('Deploy to Dev') {
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
          deployToKube(
                  namespace: 'dev-yals',
                  workloadName: 'yals-app',
                  imageRepo: env.DOCKER_REPO,
                  imageTag: dockerTag,
                  containerName: 'app'
          )
        }
      }
    }
    stage('App and UI Tests') {
      steps {
        script {
          String url;
          switch (env.BRANCH_NAME) {
            case "master":
              url = 'https://yals.eu';
              break;
            case "trunk":
              url = 'https://qa.yals.eu';
              break;
            default:
              url = 'https://dev.yals.eu';
              break;
          }
          //no tests
          if(url.equals('https://yals.eu')) { return; }

          def buildName = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}";
          withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: 'hub-creds',
                            usernameVariable: 'USR', passwordVariable: 'PASS'
                           ]]) {
            sleep(1, MINUTES)
            testApp(url: url, dParams: "-Dcom.vaadin.testbench.Parameters.hubHostname=grid.yatech.eu " +
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
      cleanWs();
    }
  }
}
