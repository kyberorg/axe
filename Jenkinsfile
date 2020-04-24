@Library('common-lib@1.4') _
pipeline {
    agent any;
    stages {
        stage('Vaadin') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'trunk') {
                        vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting')
                    } else {
                        def userInput;
                        def prodMode = false;
                        def review = false;
                        try {
                            timeout(time: 20, unit: 'SECONDS') {
                                userInput = input(message: 'Production Mode', ok: 'Build',
                                        parameters: [
                                           booleanParam(defaultValue: false, description: 'Build for Production Mode', name: 'prodMode'),
                                           booleanParam(defaultValue: false, description: 'Do code review: code-style report', name: 'review')
                                        ]);
                            }
                        } catch (err) {
                            //do nothing as default is 'false'
                        }

                        if(userInput != null) {
                            prodMode = userInput['prodMode'];
                            review = userInput['review'];
                        }

                        if (prodMode) {
                            if(review) {
                                vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting', runSiteTarget: true)
                            } else {
                                vaadin(prodModeProfile: 'production-mode', extraProfiles: 'noTesting')
                            }
                        } else {
                            if(review) {
                                vaadin(extraProfiles: 'noTesting', verbose: true, runSiteTarget: true)
                            } else {
                                vaadin(extraProfiles: 'noTesting', verbose: true)
                            }
                        }
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
                            hookUrl = 'https://docker.yatech.eu/api/webhooks/febd279f-822a-4598-a7d8-588e65851228?tag=latest';
                            break;
                        case "trunk":
                            hookUrl = 'https://docker.yatech.eu/api/webhooks/71721a7e-0d85-4735-8670-4f0afd18c0f7?tag=trunk';
                            break;
                        default:
                            hookUrl = "https://docker.yatech.eu/api/webhooks/c722e1bf-fa5a-46de-a161-1c6afdc370c1?tag=" + env.BRANCH_NAME;
                            break;
                    }
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
