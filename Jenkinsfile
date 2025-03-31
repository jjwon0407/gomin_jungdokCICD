pipeline {
    agent any
    environment {
        PROJECT_ID = 'success-project-450113'       // GCP 프로젝트 ID
        CLUSTER_NAME = 'gomin-jungdok'                  // GKE 클러스터 이름
        LOCATION = 'asia-northeast3-a'         // 클러스터 위치
        CREDENTIALS_ID = '7bf84d9c-14da-4d7a-92c4-6c98c061b523'     // GCP  인증 정보 (Jenkins에서 설정한 Google 서비스 계정 키 파일)
        DOCKER_IMAGE = 'jjwon0407/gomin_jungdok:${BUILD_ID}'  // Docker 이미지  이름
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "$JAVA_HOME/bin:$PATH"

        DB_URL = credentials('DB_URL')
        DB_USERNAME = credentials('DB_USERNAME')
        DB_PASSWORD = credentials('DB_PASSWORD')
        GCS_NAME = credentials('GCS_NAME')
        GCS = credentials('GCS')  
    }
    tools {
        jdk 'openjdk-17-jdk'
    }
    stages {
        stage("Checkout code") {
            steps {
                script {
                    //Git 리포지토리에서 코드를 체크아웃합니다.
                    git url: 'https://github.com/jjwon0407/gomin_jungdokCICD.git', branch: 'develop'
                }
            }
        }
        stage('Grant execute permission to gradlew') {
            steps {
                script {
                    //gradlew 파일이 있는 디렉토리로 이동
                    dir('backend') {
                        sh 'chmod +x ./gradlew'  // 권한 부여
                    }
                }
            }
        }
        stage('Build JAR') {
            steps {
                script {
                     dir('backend') {
                        sh 'echo $JAVA_HOME'  // JAVA_HOME을 출력
                        sh 'java -version'    

                        echo "DB_URL: ${DB_URL}"
                        echo "DB_USERNAME: ${DB_USERNAME}"

                        sh './gradlew clean build' // backend 디렉토리에서 JAR 파일 빌드
                    }
                }
            }
        }
        stage('Verify JAR File') {
            steps {
                dir('backend') {
                    sh 'ls -l build/libs/' // backend 디렉토리 내의 JAR 파일 목록 확인
                }
            }
        }

        stage("Build image") {
            steps {
                script {
                    // Docker 이미지를 빌드합니다.
                    sh "docker build -t jjwon0407/gomin_jungdok:${BUILD_ID} ."
                }
            }
        }

	stage('Test Docker Image') {
            steps {
                script {
                    try {
                        sh 'docker run -d -p 8080:8080 --name gomin_jungdok_jenkins${BUILD_ID} jjwon0407/gomin_jungdok:${BUILD_ID}'
                        sh 'sleep 5 && curl -f http://34.47.92.139:8080/ || exit 1'
                        echo "Container is running correctly."
                    } catch (Exception e) {
                        echo "Test failed. Image will not be pushed."
                        error "Stopping pipeline due to test failure."
                    }
                }
            }
        }

        stage("Push Docker image") {
            steps {
                script {
                    // Docker Hub에 이미지를 푸시합니다.
                    withDockerRegistry([credentialsId: 'jjwon0407', url: 'https://registry.hub.docker.com']) {
                        sh "docker push jjwon0407/gomin_jungdok:${BUILD_ID}"
                    }
                }
            }
        }

        stage('Deploy to GKE') {
		    when {
			    branch 'develop'
		    }
		    steps {
                script {
                    sh "sed -i 's/jjwon0407\\/gomin_jungdok:latest/jjwon0407\\/gomin_jungdok:${BUILD_ID}/g' deployment.yaml"
                    // 배포 전에 deployment.yaml 파일의 이미지를 최신 빌드 ID로 교체합니다.	

                    // Kubernetes Engine에 배포합니다.
                    step([$class: 'KubernetesEngineBuilder',
                          projectId: env.PROJECT_ID,
                          clusterName: env.CLUSTER_NAME,
                          location: env.LOCATION,
                          manifestPattern: 'deployment.yaml',
                          credentialsId: env.CREDENTIALS_ID,
                          verifyDeployments: true])
                }
            }
        }

        stage('Verify Kubernetes Deployment') {
            steps {
                script {
                    // 배포 후 애플리케이션 상태 확인 (Pod 상태 체크)
                    sh "kubectl get pods -l app=gomin-jungdok"
                }
            }
        }
    }

	post {
        always {
            script {
                sh 'docker stop gomin_jungdok_jenkins${BUILD_ID} || true'
                sh 'docker rm gomin_jungdok_jenkins${BUILD_ID} || true'
            }
            echo 'Pipeline completed.'
        }
        failure {
            script {
                echo "Build failed. Deleting the Docker image."
                sh 'docker rmi $DOCKER_IMAGE || true'
            }
        }
        success {
            echo 'Pipeline succeeded!'
        }
    }

}