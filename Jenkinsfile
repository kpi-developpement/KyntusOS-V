pipeline {
    agent any

    environment {
        // Smiya jdida bach Docker y-3ref had l'ecosysteme w ma-y-kheltouch m3a l-khrin
        COMPOSE_PROJECT_NAME = "kyntus-os-v-prod"
    }

    stages {
        stage('🧹 Clean & Checkout') {
            steps {
                script {
                    echo "=> [ÉTAPE 1] Nettoyage w téléchargement dyal l'Code mn GitHub..."
                    cleanWs()
                    checkout scm
                }
            }
        }

        stage('🛑 Teiyya7 l-9dim (Free Ports)') {
            steps {
                script {
                    echo "=> [ÉTAPE 2] Arrêt dyal l'ancienne version bach n-khewiw les ports..."
                    // 'down' kat-teiyya7 l'containers l9dam, w '|| true' bach ma-y-crashich ila l9ahom deja tay7in
                    sh "docker compose -f docker-compose.yml down || true"
                }
            }
        }

        stage('🚀 Build & Deploy Jdid') {
            steps {
                script {
                    echo "=> [ÉTAPE 3] Lancement dyal l'architecture jdida..."
                    sh "docker compose -f docker-compose.yml up -d --build"
                }
            }
        }

        stage('🛡️ Risk Management (Clean Up)') {
            steps {
                script {
                    echo "=> [ÉTAPE 4] Nettoyage dyal les vieilles images Docker..."
                    sh "docker image prune -f"
                }
            }
        }
    }

    post {
        success {
            echo "✅ DÉPLOIEMENT KYNTUS OS-V RÉUSSI ABRO!"
            echo "L'ancienne version t-ms7at w l-jdida kheddama f nafs les ports."
        }
        failure {
            echo "❌ ÉCHEC DU DÉPLOIEMENT. Dkhol l'logs dyal Jenkins t-vérifier."
        }
    }
}