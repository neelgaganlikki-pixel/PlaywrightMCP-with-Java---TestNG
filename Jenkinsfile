pipeline {

    agent any

    stages {

        stage('Run OrangeHRM Tests') {
            steps {
                bat 'mvn clean test "-Dsurefire.suiteXmlFiles=testngOrangeHRMTests.xml"'
            }
        }
    }

    post {

        always {

            emailext(
                subject: "Playwright Java - Build #${BUILD_NUMBER} - ${currentBuild.currentResult}",

                body: """
Playwright Java Automation

Build: #${BUILD_NUMBER}
Job: ${JOB_NAME}
Status: ${currentBuild.currentResult}

Build URL:
${BUILD_URL}
""",

                to: 'neelgaganat97@gmail.com'
            )
        }
    }
}
