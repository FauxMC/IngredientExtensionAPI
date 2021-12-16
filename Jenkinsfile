#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk "jdk-17.0.1"
    }
    environment {
        CURSEFORGE_API_TOKEN     = credentials('jared-curseforge-token')
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                echo 'Building'
                sh './gradlew build'
            }
        }
        stage('Publish') {
            stages {
                steps {
                    echo 'Deploying to Maven'
                    sh './gradlew publish'
                }
                steps {
                    echo 'Deploying to CurseForge'
                    sh './gradlew publishCurseForge'
                }
            }
        }
    }
    post {
        always {
            archive 'build/libs/**.jar'
            archive 'changelog.md'
        }
    }
}
