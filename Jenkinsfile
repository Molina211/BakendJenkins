pipeline {
    agent any
    stages {
        stage('Clonar') {
            steps {
                git branch: 'BackendJenkins', url: 'https://github.com/Molina211/programacion-movil-2025-a-g2.git'
            }
        }
        stage('Compilar') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t miapp .'
            }
        }
        stage('Desplegar') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d --build'
            }
        }
    }
    post {
        always {
            echo 'Pipeline completado'
            sh 'docker ps'
        }
        failure {
            echo 'El pipeline ha fallado'
        }
    }
}
