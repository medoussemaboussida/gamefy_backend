pipeline {
    agent any
    stages {
        stage('Checkout GIT') {
            steps {
                echo 'Pulling ...'
                git branch: 'main',
                    url: 'https://github.com/medoussemaboussida/gamefy_backend.git'
            }
        }
        stage("SonarQube Analysis") {
            steps {
                withSonarQubeEnv('volume') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

}
}