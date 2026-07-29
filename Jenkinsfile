pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = "crpi-4qtdo3aa6148r2ys.cn-guangzhou.personal.cr.aliyuncs.com"
        DOCKER_NAMESPACE = "devops-demos"
        IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/devops-demo-app"
        PREVIOUS_IMAGE = ""
    }

    stages {
        stage("Checkout")       { steps { checkout scm } }
        stage("Build")          { steps { sh "mvn clean package -DskipTests" } }
        stage("Docker Build")   {
            steps { script { docker.build("${IMAGE_NAME}:${env.BUILD_NUMBER}") } }
        }
        stage("Push Aliyun")    {
            steps { script {
                docker.withRegistry("https://${DOCKER_REGISTRY}", "aliyun-docker-registry") {
                    docker.image("${IMAGE_NAME}:${env.BUILD_NUMBER}").push()
                    docker.image("${IMAGE_NAME}:${env.BUILD_NUMBER}").push("latest")
                    docker.image("${IMAGE_NAME}:${env.BUILD_NUMBER}").push("dev")
                }
            } }
        }

        stage("CD Deploy")      {
            steps { script {
                env.PREVIOUS_IMAGE = sh(script: "docker inspect devops-demo-app --format='{{.Config.Image}}' 2>/dev/null || true", returnStdout: true).trim()
                sh """ docker pull ${IMAGE_NAME}:latest
                       docker stop devops-demo-app 2>/dev/null || true
                       docker rm devops-demo-app 2>/dev/null || true
                       docker run -d --name devops-demo-app --network monitoring -p 8080:8080 ${IMAGE_NAME}:latest """
            } }
        }
    }

    post {
        success { echo "Deployed TAG: ${env.BUILD_NUMBER}" }
        failure {
            script { if (env.PREVIOUS_IMAGE != null && env.PREVIOUS_IMAGE != "" && env.PREVIOUS_IMAGE.trim() != "null") {
                sh """ docker pull ${env.PREVIOUS_IMAGE}
                       docker stop devops-demo-app 2>/dev/null || true
                       docker rm devops-demo-app 2>/dev/null || true
                       docker run -d --name devops-demo-app --network monitoring -p 8080:8080 ${env.PREVIOUS_IMAGE} """
            } }
        }
    }
}
