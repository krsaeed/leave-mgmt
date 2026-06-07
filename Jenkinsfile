pipeline {
    agent any

    environment {
        IMAGE_NAME = 'leavemgmt'
        IMAGE_TAG  = '1.0'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                echo 'Building JAR artifact...'
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                sh 'docker rm -f leavemgmt-app || true'
                sh 'docker compose up -d --no-deps --build app'
            }
        }

    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}