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
                archiveArtifacts '**/*.apk'
            }
        }
        stage('Docker build to Image') {
                steps {
                    script{
                            imageName = docker.build "vaibhavi1998/expensetracker:latest"
                    }
                }
            }
            stage('Push Docker Image to DockerHub') {
                steps {
                    script{
                            docker.withRegistry('','docker credential')
                            {
                                imageName.push()
                            }
                    }
                }
            }
            stage('Pull Docker Image') {
                steps {
                   ansiblePlaybook becomeUser: null, colorized: true, disableHostKeyChecking: true, installation: 'Ansible', inventory: 'deploy-docker/inventory', playbook: 'deploy-docker/deploy-image.yml', sudoUser: null
                }
            }
        stage('DISTRIBUTE') {
            steps {
                appCenter apiToken: 'c558b8d8f6af88d97e246cf9fe104069f00f59a3',
                        ownerName: 'vaibhavitikone',
                        appName: 'ExpenseTracker',
                        pathToApp: '**/*.apk',
                        distributionGroups: 'developer'
            }
        }
    }
}