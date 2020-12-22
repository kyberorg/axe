@Library('common-lib@1.8') _

//global vars
def dockerTag = 'dev';
def dockerTags = [];
def dockerFile = 'Dockerfile';

def deployTarget = 'Dev';
def deployCreds = '';
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
    booleanParam(name: 'DEMO_BUILD', defaultValue: false, description: 'Deploy to Demo')
    booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Do NOT run Tests')
  }

  stages {
    stage('Build') {
      when {
        not {
          anyOf {
            changeRequest target: 'trunk'
            expression {
              return params.REVIEW;
            }
          }
        }
      }
      steps {
        vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting', runSiteTarget: false)
      }
    }

    stage('Build with review') {
      when {
        anyOf {
          changeRequest target: 'trunk'
          expression {
            return params.REVIEW;
          }
        }
      }
      steps {
        vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting', runSiteTarget: true)
        publishHTML([
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'target/site',
                reportFiles          : 'checkstyle.html',
                reportName           : 'HTML Report',
                reportTitles         : 'Code Style Review'
        ])
      }
    }

    stage('Dev Build') {
      when {
        not {
          anyOf {
            branch 'trunk'
            changeRequest target: 'trunk'
            buildingTag()
            expression {
              return params.DEMO_BUILD
            }
          }
        }
      }
      steps {
        script {
          deployTarget = 'Dev';
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
          dockerFile = 'Dockerfile.DEV'
        }
        script {
          deployCreds = 'dev-yals';
          deployWorkloadName = 'yals-app';
        }
        script {
          testEnabled = !params.SKIP_TESTS;
          testUrl = "https://dev.yals.eu";
        }
      }
    }

    stage('Demo Build') {
      when {
        allOf {
          not {
            buildingTag()
          }
          anyOf {
            branch 'trunk'
            changeRequest target: 'trunk'
            expression {
              return params.DEMO_BUILD
            }
          }
        }
      }
      steps {
        script {
          deployTarget = 'Demo';
        }
        script {
          def customDockerTag = params.DOCKER_TAG;
          if (!customDockerTag.trim().equals("")) {
            dockerTag = customDockerTag;
          } else if (env.BRANCH_NAME == 'trunk') {
            dockerTag = 'trunk';
          } else {
            dockerTag = 'demo';
          }
          dockerTags << dockerTag;
        }
        script {
          dockerFile = 'Dockerfile.PROD'
        }
        script {
          deployCreds = 'qa-yals';
          deployWorkloadName = 'yals-app';
        }
        script {
          testEnabled = !params.SKIP_TESTS;
          testUrl = "https://qa.yals.eu";
        }
      }
    }

    stage('Tag Build') {
      when {
        buildingTag()
      }
      steps {
        script {
          deployTarget = input message: 'Select deploy target', ok: 'Deploy!',
                  parameters: [choice(name: 'DEPLOY_TARGET', choices: 'PROD\nDemo\nDev', description: 'What is the server we deploy to?')]
          testEnabled = input message: 'Do you want to test', ok: 'Go next!',
                  parameters: [booleanParam(name: 'TEST_ENABLED', defaultValue: false, description: 'Enable if you want to test after deploy')]
        }
        script {
          def customDockerTag = params.DOCKER_TAG;
          if (!customDockerTag.trim().equals("")) {
            dockerTag = customDockerTag;
          } else {
            dockerTag = env.BRANCH_NAME;
          }
          if (deployTarget.equalsIgnoreCase("PROD")) {
            dockerTags << "latest";
          }
          dockerTags << dockerTag;
        }
        script {
          dockerFile = 'Dockerfile.PROD'
        }
      }
    }

    stage('Docker') {
      steps {
        script {
          dockerBuild(repo: env.DOCKER_REPO, tags: dockerTags, dockerFile: dockerFile);
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
          print 'Deploying to ' + deployTarget;
          script {
            if (deployTarget.equalsIgnoreCase("PROD")) {
              deployCreds = 'prod-yals-deploy-creds';
              deployWorkloadName = 'yals-app';
              testUrl = "https://yals.eu";
            } else if (deployTarget.equalsIgnoreCase("Demo")) {
              deployCreds = 'qa-yals-deploy-creds';
              deployWorkloadName = 'yals-app';
              testUrl = "https://demo.yals.eu";
            } else {
              deployCreds = 'dev-yals-deploy-creds';
              deployWorkloadName = 'yals-app';
              testUrl = "https://dev.yals.eu";
            }
          }
          script {
            withCredentials([string(credentialsId: deployCreds, variable: 'HOOK')]) {
                  deployLocation = '${HOOK}?tag='+ dockerTag;
            }
          }
          echo deployLocation;
          deployToSwarm(hookUrl: deployLocation)
        }
      }
    }

    stage("Wait For Deploy prior Testing") {
      when {
        expression {
          return testEnabled
        }
      }
      steps {
        echo 'Waiting for deployment to complete prior starting smoke testing'
        script {
          try {
            timeout(time: 2, unit: 'MINUTES') {
              input message: 'Shall we proceed?', ok: 'Yes'
            }
          }
          catch (error) {
            def user = error.getCauses()[0].getUser()
            if ('SYSTEM' == user.toString()) { // SYSTEM means timeout.
              echo "Timeout reached, continue to next stage"
            } else {
              throw new Exception("[ERROR] stage failed!")
            }
          }
        }
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
          GString buildName = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}";
          testApp(url: testUrl, dParams: "-Dgrid.hostname=http://127.0.0.1:4444 " +
                  '-Dselenide.browser=chrome ' +
                  "-Dtest.buildName=${buildName} " +
                  "-Dsurefire.rerunFailingTestsCount=2",
                  actions: 'clean test',
                  artifacts: "target/reports/**/*.png", failStep: false);
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
