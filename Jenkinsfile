pipeline {
    agent any
    stages {
        stage('Clonar') {
            steps {
                git branch: 'main', url: 'https://github.com/Molina211/BakendJenkins.git'
            }
        }
        stage('Permisos') {
            steps {
                sh 'chmod +x mvnw'
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
