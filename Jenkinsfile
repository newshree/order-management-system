pipeline {

    agent any

    tools {
        maven 'Maven'
        jdk 'Java17'
    }

    stages {

        stage('Verify Java Version') {
            steps {
                bat 'java -version'
            }
        }

        stage('Verify Maven') {
            steps {
                bat 'mvn -version'
            }
        }

        stage('Build API Gateway') {
            steps {
                dir('api-gateway') {
                    bat 'mvn clean install'
                }
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    bat 'mvn clean install'
                }
            }
        }

        stage('Build Cart Service') {
            steps {
                dir('cart-service') {
                    bat 'mvn clean install'
                }
            }
        }

        stage('Run Cart Tests') {
            steps {
                dir('cart-service') {
                    bat 'mvn test'
                }
            }
        }
    }

    post {

        success {
            echo 'OMS Pipeline Completed Successfully!'
        }

        failure {
            echo 'OMS Pipeline Failed!'
        }
    }
}