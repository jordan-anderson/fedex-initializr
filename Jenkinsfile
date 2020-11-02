library 'reference-pipeline'
library 'AppServiceAccount'

pipeline{

    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '05'))
    }

    tools {
      jdk 'JAVA_8'
      maven 'Maven 3.3.3'
    }

    parameters {
        // the default is always the first item in the list
        choice(name: 'CF_ENV', choices: 'default\ndevelopment\nrelease\nstaging\nproduction', description: 'Target Environment')
      }


    environment {
          CF_APP_NAME = "cpms-systemForm-service"
          CF_PAM_ID = "123456"
          EAI_NUMBER = "123456"
          EAI_NAME = ""
          /* EAI Folder is needed for Nexus IQ. You can go to keyword 'cicd', login and enter your EAI and then select nexusIQ to onboard. You will get an email with EAI Folder name. */
          EAI_FOLDER_NAME = ""
          /* Employee numbers who can approve the deployment into production */
          APP_CM_EMP_IDS=""
          NEXUS_CREDS_ID=''

          /* Get the channel URL from Microsoft Teams that you have created for your team*/
          TEAMS_WEBHOOK = ""
          /* credentials is a method that will take credentials id as the argument which will return user id and password in JSON format. You will need to add the credentials in the Jenkins credentials. */
          /* We will set the credentials as environment variables while deploying to PCF (in deploy method). We will append the credentials vairable with _USR and _PSW for user id and password respectively*/
          DEV_DB_CREDS=credentials('')
      REL_DB_CREDS=credentials('')
      STAGE_DB_CREDS=credentials('')
      PROD_DB_CREDS=credentials('')
      DEV_DB_URL_SECRET=credentials('')//Do not leave empty and ensure its a valid credentials in jenkins. If you are not hiding your db url then you can remove this variable.
      REL_DB_URL_SECRET=credentials('')//Do not leave empty and ensure its a valid credentials in jenkins. If you are not hiding your db url then you can remove this variable.
      STAGE_DB_URL_SECRET=credentials('')//Do not leave empty and ensure its a valid credentials in jenkins. If you are not hiding your db url then you can remove this variable.
      PROD_DB_URL_SECRET=credentials('')//Do not leave empty and ensure its a valid credentials in jenkins. If you are not hiding your db url then you can remove this variable.


          /* Leave the following default values */
          APP_VERSION = ""
          GIT_BRANCH = "${env.BRANCH_NAME}"
          APP_GROUP = ""
          JAR_PATH = ""
          NEXUS_REPO = ""
          RELEASE_FLAG = false
          APPD_PLAN = "fedex1-test"
          REPLICAS = 1

      }


    stages {

   stage('Initialize'){
          steps{
              script{
                  APP_GROUP = readMavenPom().getGroupId()
                  APP_VERSION = readMavenPom().getVersion()
                  println "App version is ${APP_VERSION}"
                  println "Group is ${APP_GROUP}"

                  if(GIT_BRANCH.contains('master')) {
                      NEXUS_REPO = "release"
                      NEXUS_VERSION="${APP_VERSION}"
                      RELEASE_FLAG = true
                  }else{
                      NEXUS_REPO = "snapshot"
                      NEXUS_VERSION="${APP_VERSION}-SNAPSHOT"
                  }

                  println "Nexus Repo is ${NEXUS_REPO}"
              }
          }
      }


   stage('Build') {
        when {
                      /* There are different branching patterns. Here I'm maintaining develop and master branches only.
                      Develop will commit to development space and master will deploy to release space using webhook.
                      We are deploying to staging (L6) and production using build parameters (manual).
                      We do not build for stage and production, instead we pull the artifact which was uploaded to nexus and deploy.
                      */
          not {
            anyOf {
              environment name: 'CF_ENV', value: 'staging'
              environment name: 'CF_ENV', value: 'production'
            }
          }
        }
        steps {
          println "Building source from branch ${GIT_BRANCH}"
          sh 'chmod +x mvnw'
sh 'mvn clean package'

        }
      }


   stage('SonarQube & NexusIQ') {
     parallel {
        stage('SonarQube') {
      when {
        anyOf {
          environment name: 'CF_ENV', value: 'development'
          branch 'develop'
        }
        not {
          anyOf {
            environment name: 'CF_ENV', value: 'release'
            environment name: 'CF_ENV', value: 'staging'
            environment name: 'CF_ENV', value: 'production'
            branch 'master'
          }
        }
      }
      steps {
                    sonarqube projectName: "${CF_APP_NAME}",
                              projectKey: "${APP_GROUP}",
                              projectVersion: "${APP_VERSION}",
                              src: 'src/main', 
                              test: 'src/test', 
                              binaries: "target", 
                              repo: 'git', 
                              scmDisabled: 'false', 
                              exclusions: '**/src/test/*', 
                              junitPath: 'build/test-results/test', 
                              jacocoPath: 'build/jacoco/test.exec'
      }
    }

        stage('NexusIQ') {
        when {
          anyOf {
            environment name: 'CF_ENV', value: 'development'
            branch 'develop'
          }
          not {
            anyOf {
              environment name: 'CF_ENV', value: 'release'
              environment name: 'CF_ENV', value: 'staging'
              environment name: 'CF_ENV', value: 'production'
              branch 'master'
            }
          }
        }
        steps {
          nexusPolicyEvaluation iqApplication: "${EAI_FOLDER_NAME}", iqStage: 'build'
        }
      }

     }
   }

   stage('Nexus Staging') {
        when {
          anyOf {
            environment name: 'CF_ENV', value: 'development'
            environment name: 'CF_ENV', value: 'release'
            branch 'develop'
            branch 'master'
          }
          not {
            anyOf {
              environment name: 'CF_ENV', value: 'staging'
              environment name: 'CF_ENV', value: 'production'
            }
          }
        }
        steps {

          println "Uploading jar to Nexus ${CF_APP_NAME}"
          nexusArtifactUploader artifacts: [[artifactId: "${CF_APP_NAME}", classifier: '', file: "target/${CF_APP_NAME}-${APP_VERSION}.jar", type: 'jar']],
            credentialsId: "${NEXUS_CREDS_ID}",
            groupId: "eai${EAI_NUMBER}.${APP_GROUP}",
            nexusUrl: 'nexus.prod.cloud.fedex.com:8443/nexus',
            nexusVersion: 'nexus3',
            protocol: 'https',
            repository: "${NEXUS_REPO}",
            version: "${NEXUS_VERSION}"

        }
      }


   stage('Nexus Pull') {
        steps {
          println "Downloading from nexus repo..."
          downloadNexusArtifact groupId: "eai${EAI_NUMBER}.${APP_GROUP}",
              artifactId: "${CF_APP_NAME}",
              repo:"${NEXUS_REPO}",
              release: "${RELEASE_FLAG}".toBoolean(),
              extension: "jar",
              version: "${NEXUS_VERSION}",
              downloadFileName: "${CF_APP_NAME}.jar"
          }
      }


   stage('Deploy to Development') {
  when {
    anyOf {
      environment name: 'CF_ENV', value: 'development'
      branch 'develop'
    }
    not {
      anyOf {
        environment name: 'CF_ENV', value: 'release'
        environment name: 'CF_ENV', value: 'staging'
        environment name: 'CF_ENV', value: 'production'
      }
    }
  }
  steps {
    deployToPCF("development", "")
  }
}


   stage('Deploy to Release') {
  when {
    anyOf {
      environment name: 'CF_ENV', value: 'release'
      branch 'master'
    }
    not {
      anyOf {
        environment name: 'CF_ENV', value: 'staging'
        environment name: 'CF_ENV', value: 'production'
      }
    }
  }
  steps {
    deployToPCF("release", "")
  }
}


   stage('Deploy to Staging') {
  when {
    anyOf {
      environment name: 'CF_ENV', value: 'staging'
    }
  }
  steps {
    deployToPCF("staging", "")
  }
}


   stage('Deploy to Production') {
  when {
    anyOf {
      environment name: 'CF_ENV', value: 'production'
    }
  }
  steps {
    deployToPCF("production", "")
  }
}


}


    post {
        success {
          office365ConnectorSend color: '#00FF00', message: "SUCCESSFUL", status: "Build Success", webhookUrl: TEAMS_WEBHOOK
        }
        failure {
          office365ConnectorSend color: '#FF0000', message: "FAILED", status: "Build Failure", webhookUrl: TEAMS_WEBHOOK
        }
      }


}

def deployToPCF(String cloudSpace, String foundation) {
    CF_ENV = cloudSpace
      CF_API = "https://api.sys.${foundation}.paas.fedex.com"
      JAR_PATH = "${CF_APP_NAME}.jar"

      if(CF_ENV.equalsIgnoreCase('development')){
          CF_LEVEL = "dev"
          ENV_PROPERTIES = ["CONFIG_URI":"", "DB_URL": "${DEV_DB_URL_SECRET}","DB_USERNAME" : "${DEV_DB_CREDS_USR}","DB_PASSWORD" : "${DEV_DB_CREDS_PSW}"]
      }else if(CF_ENV.equalsIgnoreCase('release')){
          CF_LEVEL = "rel"
          ENV_PROPERTIES = ["CONFIG_URI":"", "DB_URL": "${REL_DB_URL_SECRET}","DB_USERNAME" : "${REL_DB_CREDS_USR}","DB_PASSWORD" : "${REL_DB_CREDS_PSW}"]
      }else if(CF_ENV.equalsIgnoreCase('staging')){
          CF_LEVEL = "stage"
          APPD_PLAN = "fedex1"
          ENV_PROPERTIES = ["CONFIG_URI":"", "DB_URL": "${STAGE_DB_URL_SECRET}","DB_USERNAME" : "${STAGE_DB_CREDS_USR}","DB_PASSWORD" : "${STAGE_DB_CREDS_PSW}"]
      }else if(CF_ENV.equalsIgnoreCase('production')){
          CF_LEVEL = "prod"
          APPD_PLAN = "fedex1"
          REPLICAS = 3
          ENV_PROPERTIES = ["CONFIG_URI":"", "DB_URL": "${PROD_DB_URL_SECRET}","DB_USERNAME" : "${PROD_DB_CREDS_USR}","DB_PASSWORD" : "${PROD_DB_CREDS_PSW}"]
      }


      println 'Deploying to PCF'

      pcfBlueGreenDeploy pamId: "${CF_PAM_ID}",
          url: "${CF_API}",
          stage: "${CF_ENV}",
          appName: "${CF_APP_NAME}",
          instances: "${REPLICAS}",
          jarPath: "${JAR_PATH}",
          foundation: "${foundation}",
          appdPlan: "${APPD_PLAN}",
          eaiNum: "${EAI_NUMBER}",
          eaiAppName: "${EAI_NAME}",
          props: ENV_PROPERTIES,
          javaOpts: "-Duser.timezone=GMT -Dspring.profiles.active=${CF_LEVEL}",
          smoke:{
              echo "Update your smoke test cases here"
              //def URL = "https://${CF_APP_NAME}-${CF_LEVEL}-tmp.app.${foundation}.paas.fedex.com/api/v1/service/"
              //def VerifyText = "My APP is UP!"
              //def SERVICE_STATUS = sh (script: "curl -f -s ${URL} > /dev/null", returnStatus: true)
              //echo "$SERVICE_STATUS"
              //if( SERVICE_STATUS != 0  )
                            //error "Unable to connect to service (${SERVICE_STATUS}): ${URL}"
              //def SERVICE_BODY = sh ( script: "curl -s ${URL}", returnStdout: true ).trim()
              //assert SERVICE_BODY.contains(VerifyText) : "JSON Does not contain '${VerifyText}' for ${URL}"
          }

}

