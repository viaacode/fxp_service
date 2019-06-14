
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
           withMaven(
            // Maven installation declared in the Jenkins "Global Tool Configuration"
            maven: 'M3',
            // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
            // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
            mavenSettingsConfig: '452256a3-4cec-48ed-9194-8437ff991435',
            mavenLocalRepo: '.repository') {
              // Run the maven build
              sh "mvn deploy"
            }
        }
    }
}