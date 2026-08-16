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

        junit 'target/surefire-reports/*.xml'

        script {

            def reportResult = powershell(
                returnStdout: true,
                script: '''
                    [xml]$xml = Get-Content "target/surefire-reports/TEST-TestSuite.xml"

                    $total = [int]$xml.testsuite.tests
                    $failed = [int]$xml.testsuite.failures
                    $skipped = [int]$xml.testsuite.skipped
                    $passed = $total - $failed - $skipped

                    Write-Output "TOTAL=$total"
                    Write-Output "PASSED=$passed"
                    Write-Output "FAILED=$failed"
                    Write-Output "SKIPPED=$skipped"

                    Write-Output "FAILED_TESTS_START"

                    $xml.testsuite.testcase | ForEach-Object {

                        if ($_.failure -or $_.error) {
                            Write-Output "$($_.classname).$($_.name)"
                        }
                    }

                    Write-Output "FAILED_TESTS_END"
                '''
            ).trim()

            def total = 0
            def passed = 0
            def failed = 0
            def skipped = 0
            def failedTests = []

            def readingFailedTests = false

            reportResult.split("\\r?\\n").each { line ->

                if (line.startsWith("TOTAL=")) {
                    total = line.substring(6).toInteger()
                }

                else if (line.startsWith("PASSED=")) {
                    passed = line.substring(7).toInteger()
                }

                else if (line.startsWith("FAILED=")) {
                    failed = line.substring(7).toInteger()
                }

                else if (line.startsWith("SKIPPED=")) {
                    skipped = line.substring(8).toInteger()
                }

                else if (line == "FAILED_TESTS_START") {
                    readingFailedTests = true
                }

                else if (line == "FAILED_TESTS_END") {
                    readingFailedTests = false
                }

                else if (readingFailedTests && line.trim()) {
                    failedTests.add(line.trim())
                }
            }

            def failedTestText

            if (failedTests) {

                failedTestText = failedTests.collectWithIndex {
                    testName, index ->
                        "${index + 1}. ${testName}"
                }.join("\n")

            } else {

                failedTestText = "No failed tests."
            }

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
${failedTestText}

BUILD INFORMATION
-----------------------------------------------
Job               : ${JOB_NAME}
Build URL         : ${BUILD_URL}
Git Commit        : ${GIT_COMMIT ?: 'N/A'}
Branch            : ${GIT_BRANCH ?: 'main'}

================================================
""",

                to: 'neelgaganat97@gmail.com'
            )
        }
    }
}
