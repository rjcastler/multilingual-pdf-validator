pipeline {
    agent any

    tools {
        jdk 'JDK17'
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

        stage('Build & PDF Validation') {
            steps {
                sh 'mvn -B clean test'
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
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/pdf-validation-report',
                    reportFiles: 'report.html',
                    reportName: 'Multilingual PDF Validation Report',
                    includes: 'report.html'
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
            cleanWs()
        }
    }
}
