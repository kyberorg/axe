@Library('common-lib@1.7') _
pipeline {
    agent any;
    environment {
      DOCKER_REPO = 'yadev/yals'
    }
    parameters {
        booleanParam(name: 'REVIEW', defaultValue: false, description: 'Do code review: code-style report')
        string(name: 'DOCKER_TAG', defaultValue: "")
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
                    def dockerTag = params.DOCKER_TAG;
                    def tags = [];
                    String tag;
                    if (! dockerTag.trim().equals("")) {
                        tag = dockerTag;
                    } else if (env.BRANCH_NAME.equals("master")) {
                        tag = "latest";
                    } else {
                        tag = env.BRANCH_NAME;
                    }
                    tags << tag;

                    dockerBuild(repo: env.DOCKER_REPO, tags: tags);
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
                  deployToKube(
                          namespace: 'dev-yals',
                          workloadName: 'yals-app',
                          imageRepo: env.DOCKER_REPO,
                          imageTag: env.BRANCH_NAME,
                          containerName: 'app'
                  )
                }
            }
        }
        /*stage('App and UI Tests') {
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
        }*/
    }
    post {
        always {
            cleanWs();
        }
    }
}
