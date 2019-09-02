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
                     vaadin(extraProfiles: 'noTesting')
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
                 // Not enabled yet
                 //deployToSwarm(hookUrl: hookUrl);
               }
            }
        }
        stage('UI Tests') {
            steps {
               script {
                 String url;
                 switch(env.BRANCH_NAME) {
                    case "latest":
                        url = 'https://yals.ee';
                        break;
                    case "trunk":
                        url = 'https://qa.yals.eu';
                        break;
                    default:
                        url = 'https://dev.yals.eu';
                        break;
                 }
                 // Not enabled yet
                 //smartWait(url: url);
                 //testApp(url: url);
               }
            }
        }
    }
}