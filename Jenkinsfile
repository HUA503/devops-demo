pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = "registry.cn-hangzhou.aliyuncs.com"
        DOCKER_NAMESPACE = "<你的命名空间>"
        IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/devops-demo-app"
        PREVIOUS_IMAGE = ""
    }

    stages {
        stage("Checkout")       { steps { checkout scm } }
        stage("Build")          { steps { sh "mvn clean package -DskipTests" } }

        stage("Trivy Base Scan"){
            steps { script { sh "docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL --exit-code 0 eclipse-temurin:17-jre-alpine" } }
        }

        stage("Docker Build")   {
            steps { script { docker.build("${IMAGE_NAME}:${env.BUILD_NUMBER}") } }
        }

        stage("Trivy Image Scan"){
            steps { script { sh "docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL --exit-code 1 ${IMAGE_NAME}:${env.BUILD_NUMBER}" } }
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
                PREVIOUS_IMAGE = sh(script: "docker inspect devops-demo-app --format='{{.Config.Image}}' 2>/dev/null || true", returnStdout: true).trim()
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
            script { if (env.PREVIOUS_IMAGE != "") {
                sh """ docker pull ${PREVIOUS_IMAGE}
                       docker stop devops-demo-app 2>/dev/null || true
                       docker rm devops-demo-app 2>/dev/null || true
                       docker run -d --name devops-demo-app --network monitoring -p 8080:8080 ${PREVIOUS_IMAGE} """
            } }
        }
    }
}
