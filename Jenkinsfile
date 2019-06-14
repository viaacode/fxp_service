
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
                sh 'mvn deploy' 
            }
        }
    }
}