pipeline {
    agent none

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 4, unit: 'HOURS')
        timestamps()
        ansiColor('xterm')
    }

    triggers {
        // Trigger once per day using hash to distribute load
        cron('H H * * *')
    }

    stages {
        stage('Build examples') {
            matrix {
                // this will provision a pod for each matrix branch combination.
                agent {
                    kubernetes {
                        label 'micrometer-registry-example-builds'
                        cloud 'linux-amd64'
                        nodeSelector 'kubernetes.io/arch=amd64,kubernetes.io/os=linux'
                        namespace 'oss-jenkins-slaves-ni'
                        instanceCap '2'
                        idleMinutes '0'
                        yamlFile '.ci/k8s/build-pod.yaml'
                    }
                }

                axes {
                    axis {
                        name 'EXAMPLE_NAME'
                        values(
                            'micrometer-spring-boot',
                            'micrometer-standalone'
                        )
                    }
                }

                stages {
                    stage('Build') {
                        steps {
                            dir("${EXAMPLE_NAME}") {
                                echo "Building ${EXAMPLE_NAME}..."
                                sh './gradlew build'
                            }
                        }
                    }
                }

                post {
                    cleanup {
                        deleteDir()
                    }
                }
            }
        }
    }

    post {
        unsuccessful {
            script {
                emailext(
                    recipientProviders: [
                        culprits(),
                        requestor()
                ],
                    subject: '$DEFAULT_SUBJECT',
                    mimeType: 'text/html',
                    body: '${SCRIPT, template = "managed:oneagent-pipeline-email.groovy"}'
                )

                if ('main' == env.BRANCH_NAME || env.BRANCH_NAME.startsWith('release/')) {
                    slackSend(message: "Build <${env.BUILD_URL}|${env.BUILD_TAG}> " + \
                        "on branch ${env.BRANCH_NAME} FAILED " +\
                        "(${currentBuild.durationString - ~/ and counting/})", \
                        color: '#ff0000')
                }
            }
        }
    }
}
