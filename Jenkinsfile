pipeline {
    environment{
        imageName = ''
    }
    agent any

    stages {
        stage('Git'){
            steps{
                git 'https://github.com/printSamarth/ExpenseTracker'
        
            }    
        }
        stage('Clean') {
            steps {
                sh './gradlew --refresh-dependencies clean'
            }
        }
        stage('Build'){
            steps{
                sh './gradlew assembleDebug'
                archiveArtifacts '**/app-debug.apk'
            }
        }
        stage('Install for testing'){
            steps{
                sh './gradlew installDebug'
            }
        }
        stage('Testing'){
            steps{
                sh './gradlew connectedDebugAndroidTest'
            }
        }
        stage('Docker build to Image') {
                steps {
                    script{
                            imageName = docker.build "ssvapp/expense-tracker:latest"
                    }
                }
            }
            stage('Push Docker Image to DockerHub') {
                steps {
                    script{
                            docker.withRegistry('','7fd7ec98-6ea4-4c67-9d94-a9cfdb4592c6')
                            {
                                imageName.push()
                            }
                    }
                }
            }
        stage('Pull Docker Image') {
                        steps {
                           ansiblePlaybook becomeUser: null, colorized: true, disableHostKeyChecking: true,
                           installation: 'Ansible', inventory: 'deploy-docker/inventory',
                           playbook: 'deploy-docker/deploy-image.yml', sudoUser: null
                        }
                    }
        stage('DISTRIBUTE') {
            steps {
                appCenter apiToken: '92a01f4cb9c3bb7a57de2984bde44c67b00f5979',
                        ownerName: 'ssvapp',
                        appName: 'ExpenseTracker',
                        pathToApp: '**/app-debug.apk',
                        distributionGroups: 'tester'
            }
        }
    }
}