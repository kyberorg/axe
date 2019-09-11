@Library('common-lib@1.0')_
pipeline {
    agent any;
    stages {
        stage('Vaadin') {
          steps {
            script {
               if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'trunk') {
                  vaadin(prodModeProfile: 'production-mode',extraProfiles: 'noTesting')
               } else {
                  def prodMode = false;
                  try {
                     timeout(time: 10, unit: 'SECONDS') {
                        prodMode = input(message: 'Production Mode', ok: 'Build',
                        parameters:[ booleanParam(defaultValue: false, description: 'Build for Production Mode', name: 'True')]);
                     }
                  } catch(err) {
                     //do nothing as default is 'false'
                  }
                  if(prodMode) {
                     vaadin(prodModeProfile: 'production-mode',extraProfiles: 'noTesting')
                  } else {
                     vaadin(extraProfiles: 'noTesting', verbose: true)
                  }
               }
            }
          }
        }
        stage('Docker') {
          steps {
            script {
                def repo = 'yadev/yals';
                def tags = [];
                String tag;
                if(env.BRANCH_NAME.equals("master")) {
                   tag = "latest";
                } else {
                   tag = env.BRANCH_NAME;
                }
                tags << tag;

                dockerBuild(repo: repo, tags: tags);
                dockerLogin(creds: 'docker-hub');
                dockerPush();
                dockerLogout();
                dockerClean();
            }
          }
        }
        stage('Deploy') {
            steps {
               script {
                 String hookUrl;
                 switch(env.BRANCH_NAME) {
                    case "latest":
                        hookUrl = 'noUrl';
                        break;
                    case "trunk":
                        hookUrl = 'https://docker.yatech.eu/api/webhooks/af079b20-1091-43ef-8708-26abe75fe509';
                        break;
                    default:
                        hookUrl = 'https://docker.yatech.eu/api/webhooks/c722e1bf-fa5a-46de-a161-1c6afdc370c1';
                        break;
                 }
                 // Not enabled yet until SpringBoot 2 migration completed
                 deployToSwarm(hookUrl: hookUrl);
               }
            }
        }
        stage('UI Tests') {
            steps {
               script {
                 String url;
                 switch(env.BRANCH_NAME) {
                    case "latest":
                        url = 'https://yals.eu';
                        break;
                    case "trunk":
                        url = 'https://qa.yals.eu';
                        break;
                    default:
                        url = 'https://dev.yals.eu';
                        break;
                 }
                 // Not enabled yet before switching to TestContainers
                 sleep(30);

                 /* def testFailed = false;
                 try {
                    sh 'mvn -Dport=7999 -DtestUrl=https://dev.yals.eu -Dtest=Debug* clean test'
                 } catch(err) {
                     //just continue to results instead of failing build just mark build as failed
                     testFailed = true;
                 }
                 junit(testResults: 'target/surefire-reports *//** /* *//*.xml', allowEmptyResults: true);
                 archiveArtifacts(artifacts: 'target *//*.flv', allowEmptyArchive: true);
                  if(testFailed) {
                       error("Tests failed")
                    } */
                 testApp(url: url);
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