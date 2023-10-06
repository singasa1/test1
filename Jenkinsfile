#!groovy

currentBuild.result = "SUCCESS"

@Library("cariad-utilities")
import cariad.CariadUtilities

/*
* Only run this pipeline for pull-requests.
*/
if (!env.CHANGE_BRANCH || !env.CHANGE_TARGET) {
    return
}

def cariad = new CariadUtilities(this)

node(cariad.DEFAULT_NODE) {
     ws(cariad.WORKSPACE_PARTNER_API) {
        try {
            stage('Checkout') {
                cariad.cleanWorkspace()
                git branch: env.CHANGE_BRANCH,
                    credentialsId: cariad.RO_CHECKOUT_CREDENTIALS,
                    url: 'https://devstack.vwgroup.com/bitbucket/scm/g21c/vendor-cariad-inc-partnerapi.git'
            }
            stage('Build') {
                sh """
                    export ANDROID_HOME=$HOME/android
                    ls -la
                    echo "Build command(s)"
                    pwd
                    current_pwd=\$(pwd)

                    cd \$current_pwd/PartnerLibrary
                    ./gradlew assembleDebug
                """
            }
            stage('Publish AAR') {
                def name = "partnerlibrary-debug.aar"
                sh """
                    cp PartnerLibrary/partnerlibrary/build/outputs/aar/${name} .
                """
                cariad.uploadArtifact(name, cariad.ARTIFACTORY_INTERNAL, "partner_api/${env.BUILD_NUMBER}")
            }
        } catch (err) {
            currentBuild.result = "FAILURE"
            throw err
        } finally {

        }
     }
}
