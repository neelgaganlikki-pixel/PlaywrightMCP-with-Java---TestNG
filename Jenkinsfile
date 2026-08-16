post {

    always {

        junit 'target/surefire-reports/*.xml'

        script {

            def total = 0
            def passed = 0
            def failed = 0
            def skipped = 0
            def failedTests = []

            def reportFiles = powershell(
                returnStdout: true,
                script: '''
                    Get-ChildItem "target/surefire-reports/TEST-*.xml" |
                    Select-Object -ExpandProperty FullName
                '''
            ).trim()

            if (reportFiles) {

                reportFiles.split("\\r?\\n").each { reportFile ->

                    def report = new XmlSlurper().parse(
                        new File(reportFile)
                    )

                    def reportTotal = report.@tests.toInteger()
                    def reportFailed = report.@failures.toInteger()
                    def reportSkipped = report.@skipped.toInteger()

                    total += reportTotal
                    failed += reportFailed
                    skipped += reportSkipped

                    report.testcase.each { testCase ->

                        if (testCase.failure.size() > 0 ||
                            testCase.error.size() > 0) {

                            failedTests.add(
                                "${testCase.@classname}.${testCase.@name}"
                            )
                        }
                    }
                }
            }

            passed = total - failed - skipped

            emailext(
                subject: "Playwright Java Automation Report - Build #${BUILD_NUMBER} - ${currentBuild.currentResult}",

                body: """
================================================
       PLAYWRIGHT JAVA AUTOMATION REPORT
================================================

Build: #${BUILD_NUMBER}
Environment: QA
Browser: Chromium

TEST SUMMARY
-----------------------------------------------
Total Tests       : ${total}
Passed            : ${passed}
Failed            : ${failed}
Skipped           : ${skipped}

FAILED TESTS
-----------------------------------------------
${failedTests
    ? failedTests.collectWithIndex { testName, index ->
        "${index + 1}. ${testName}"
      }.join("\n")
    : "No failed tests."}

BUILD INFORMATION
-----------------------------------------------
Job               : ${JOB_NAME}
Build URL         : ${BUILD_URL}
Git Commit        : ${GIT_COMMIT ?: 'N/A'}
Branch            : ${GIT_BRANCH ?: 'main'}

================================================
""",

                to: 'YOUR_EMAIL@gmail.com'
            )
        }
    }
}
