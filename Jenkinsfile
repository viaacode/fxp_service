
pipeline {
    agent {
      node {
        // spin up a pod to run this build on
        label 'maven'
      }
    }
    options {
        // set a timeout of 20 minutes for this pipeline
        timeout(time: 40, unit: 'MINUTES')
    }
    stages {
        stage ('Build') {
            steps {
              configFileProvider([configFile(fileId: '452256a3-4cec-48ed-9194-8437ff991435', variable: 'MAVEN_SETTINGS_XML')]) {
                sh 'mvn -s $MAVEN_SETTINGS_XML package'
              }
            }
        }
        stage ('Deploy') {
            steps {
              configFileProvider([configFile(fileId: '452256a3-4cec-48ed-9194-8437ff991435', variable: 'MAVEN_SETTINGS_XML')]) {
                sh 'mvn -s $MAVEN_SETTINGS_XML deploy'
              }
            }
        }
    }
}