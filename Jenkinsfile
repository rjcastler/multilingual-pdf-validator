pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run PDF Validation Tests') {
            steps {
                sh '''
            mvn clean test || TEST_RESULT=$?

            mvn surefire-report:report

            exit ${TEST_RESULT:-0}
        '''
            }
        }

        stage('Publish JUnit Results') {
            steps {
                junit allowEmptyResults: true,
                      testResults: 'target/surefire-reports/*.xml'
            }
        }

        stage('Publish HTML PDF Report') {
            steps {
                publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/reports',
                        reportFiles: 'surefire.html',
                        reportName: 'PDF Validation Report',
                        reportTitles: 'PDF Validation Test Report'
                ])
            }
        }

        stage('Archive PDF Validation Report') {
            steps {
                archiveArtifacts artifacts: 'target/pdf-validation-report/report.html',
                                 allowEmptyArchive: false,
                                 fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'PDF validation and HTML report publishing completed successfully.'
        }

        failure {
            echo 'PDF validation pipeline failed. Check the JUnit and HTML reports.'
        }

        always {
            publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/reports',
                    reportFiles: 'surefire.html',
                    reportName: 'PDF Validation Report',
                    reportTitles: 'PDF Validation Test Report'
            ])
        }
    }
}
