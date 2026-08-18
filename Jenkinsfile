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

            // Publish TestNG / Surefire results in Jenkins
            junit 'target/surefire-reports/*.xml'


            script {

                def total = 0
                def passed = 0
                def failed = 0
                def skipped = 0

                def failedTests = []


                // Check if Surefire reports exist

                def reportFiles = findFiles(
                    glob: 'target/surefire-reports/TEST-*.xml'
                )


                if (reportFiles.length > 0) {

                    def reportResult = powershell(
                        returnStdout: true,

                        script: '''
                            $total = 0
                            $failed = 0
                            $skipped = 0

                            $failedTests = @()


                            $files = Get-ChildItem "target/surefire-reports/TEST-*.xml"


                            foreach ($file in $files) {

                                [xml]$xml = Get-Content $file.FullName


                                foreach ($suite in $xml.testsuite) {

                                    $total += [int]$suite.tests

                                    $failed += [int]$suite.failures

                                    $skipped += [int]$suite.skipped


                                    foreach ($test in $suite.testcase) {

                                        if ($test.failure -or $test.error) {

                                            $failedTests += "$($test.classname).$($test.name)"

                                        }
                                    }
                                }
                            }


                            $passed = $total - $failed - $skipped


                            Write-Output "TOTAL=$total"

                            Write-Output "PASSED=$passed"

                            Write-Output "FAILED=$failed"

                            Write-Output "SKIPPED=$skipped"


                            Write-Output "FAILED_TESTS_START"


                            foreach ($test in $failedTests) {

                                Write-Output $test

                            }


                            Write-Output "FAILED_TESTS_END"
                        '''
                    ).trim()


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

                }


                else {

                    echo "WARNING: Surefire XML reports were not generated."

                    echo "Maven/TestNG execution may have failed before creating reports."

                }


                // Format failed tests

                def failedTestText = "No failed tests."


                if (failedTests.size() > 0) {

                    def counter = 1

                    def failedTestLines = []


                    failedTests.each { testName ->

                        failedTestLines.add(
                            "${counter}. ${testName}"
                        )

                        counter++

                    }


                    failedTestText = failedTestLines.join("\n")
                }


                // Send email

                emailext(

                    subject: "Java Automation Test Report - Build #${BUILD_NUMBER} - ${currentBuild.currentResult}",


                    body: """
================================================
          JAVA AUTOMATION TEST REPORT
================================================

Build: #${BUILD_NUMBER}
Environment: QA
Browser: Chrome

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
}
