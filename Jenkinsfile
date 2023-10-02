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
                ls -la
                echo "Build command(s)"
                root=$(pwd)
                partnerlibrarypath=$root/PartnerLibrary
                cd $partnerlibrarypath
                ./gradlew assembleDebug
                
                cd $root
                partnerenablerservicepath=$root/PartnerEnablerService
                cd $partnerenablerservicepath
                ./gradlew assembleDebug
            }
        } catch (err) {
            currentBuild.result = "FAILURE"
            throw err
        } finally {

        }
    }
}

