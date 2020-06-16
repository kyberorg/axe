@Library('common-lib@1.4') _
pipeline {
    agent any;
    parameters {
        booleanParam(name: 'REVIEW', defaultValue: false, description: 'Do code review: code-style report')
     }

    stages {
        stage('Vaadin') {
            steps {
                script {
                    def review = params.REVIEW

                    print "Parameters: Review = ${review}"

                    vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting', runSiteTarget: review)

                    if(review) {
                        publishHTML([
                           allowMissing: true,
                           alwaysLinkToLastBuild: false,
                           keepAll: true,
                           reportDir: 'target/site',
                           reportFiles: 'checkstyle.html',
                           reportName: 'HTML Report',
                           reportTitles: ''
                        ])
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
                    if (env.BRANCH_NAME.equals("master")) {
                        tag = "latest";
                    } else {
                        tag = env.BRANCH_NAME;
                    }
                    tags << tag;

                    dockerBuild(repo: repo, tags: tags);
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
                    String hookUrl;
                    switch (env.BRANCH_NAME) {
                        case "master":
                            hookUrl = '';
                            break;
                        case "trunk":
                            hookUrl = 'https://docker.yatech.eu/api/webhooks/2c45304e-8344-4b01-8a5a-c2828bc1abfc?tag=trunk';
                            break;
                        default:
                            hookUrl = "https://docker.yatech.eu/api/webhooks/c722e1bf-fa5a-46de-a161-1c6afdc370c1?tag=" + env.BRANCH_NAME;
                            break;
                    }
                    //no hook - no deploy
                    if(hookUrl.equals('')) { return; }
                    deployToSwarm(hookUrl: hookUrl);
                    sleep(30); //pause for application to be started
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
