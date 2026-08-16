pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

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
                    <h2>Playwright Java Automation Test Results</h2>

                    <p><b>Job:</b> ${JOB_NAME}</p>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Status:</b> ${currentBuild.currentResult}</p>

                    <p>OrangeHRM TestNG execution has completed.</p>

                    <p>
                        <a href="${BUILD_URL}">
                            View Jenkins Build
                        </a>
                    </p>
                """,
                to: 'neelgaganat97@gmail.com'
            )
        }
    }
}
