package projects.pipelines
/*
parameters
buildParameters         -> Parameters for build gradlew                             -> default: ''
runOnly                 ->  Name of Run PBA, if '' - all PBAs                       -> default: ''
artifactPath            ->  Path to artifact on Jenkins side                        -> default: 'build/libs/gradle-test-build-4.9.jar'
cred                    ->  user Credential Id                                      -> default: '4' (user1 - 1, user2 - 2, slave - 3, admin - 4)

flowConfigName          ->  CloudBees Flow configuration name on Jenkins side       -> default: 'electricflow'
flowProjectName         ->  CloudBees Flow Project Name                             -> default: 'pvNativeJenkinsProject01'
flowReleaseName         ->  CloudBees Flow Release Name                             -> default: 'pvRelease'
flowApplication         ->  CloudBees Flow Application Name                         -> default: 'pvNativeJenkinsTestApplication01'
flowApplicationProcess  ->  CloudBees Flow Application Process Name                 -> default: 'pvDeployProcess'
flowEnvironmentName     ->  CloudBees Flow Environment Name                         -> default: 'pvEnvironment'
flowArtifactoryKP       ->  CloudBees Flow Artifact Group:Name                      -> default: 'pv:RUNASTests'
flowRepositoryName      ->  CloudBees Flow target repository                        -> default: 'default'
flowPipelineName        ->  CloudBees Flow Pipeline Name                            -> default: 'pvNativeJenkinsTestPipeline01'
flowProcedureName       ->  CloudBees Flow Procedure Name                           -> default: 'nativeJenkinsTestProcedure'
flowHTTPBody            ->  CloudBees Flow HTTP Body for API request                -> default: ''
flowEnvVarNameForResult ->  CloudBees Flow Variable name for saving the results     -> default: ''
flowHTTPMethod          ->  CloudBees Flow HTTP method for API request              -> default: 'GET'
flowAPIURL              ->  CloudBees Flow HTTP Url for API request                 -> default: '/projects'
*/
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                cleanWs()
                sh 'echo "=================== Run on jenkins side ===================="'
                sh 'echo `pwd`'
                sh 'echo `whoami`'
                sh 'echo `uname -a`'
                sh 'echo `hostname`'
                sh 'echo "=================== Git and Build ===================="'
                git 'https://github.com/electric-cloud-community/gradle-test-build.git'
                sh "./gradlew clean build $buildParameters"
            }
        }
    }
    post {
        always {
            script {
                sh 'echo "=================== Post Build Actions ===================="'
                sh 'echo =====================archiveArtifacts====================='
                archiveArtifacts 'build/libs/*.jar'

                sh 'echo =====================JUnit====================='
                junit 'build/test-results/test/TEST-com.sample.test.TestService.xml'

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowAssociateBuildToRelease') {
                    sh 'echo  =====================cloudBeesFlowAssociateBuildToRelease====================='
                    cloudBeesFlowAssociateBuildToRelease configuration: "$flowConfigName", overrideCredential: [credentialId: "$cred"], projectName: "$flowProjectName", releaseName: "$flowReleaseName"
                }

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowCreateAndDeployAppFromJenkinsPackage') {
                    sh 'echo  =====================cloudBeesFlowCreateAndDeployAppFromJenkinsPackage====================='
                    cloudBeesFlowCreateAndDeployAppFromJenkinsPackage configuration: "$flowConfigName", filePath: "$artifactPath", overrideCredential: [credentialId: "$cred"]
                }

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowDeployApplication') {
                    sh 'echo  =====================cloudBeesFlowDeployApplication====================='
                    cloudBeesFlowDeployApplication applicationName: "$flowApplication", applicationProcessName: "$flowApplicationProcess", configuration: "$flowConfigName", deployParameters: '{"runProcess":{"applicationName":"$flowApplication","applicationProcessName":"$flowApplicationProcess","parameter":[{"actualParameterName":"deployTestAppParam1","value":""},{"actualParameterName":"deployTestAppParam2","value":""}]}}', environmentName: "$flowEnvironmentName", overrideCredential: [credentialId: "$cred"], projectName: "$flowProjectName"
                }

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowPublishArtifact') {
                    sh 'echo  =====================cloudBeesFlowPublishArtifact====================='
                    cloudBeesFlowPublishArtifact artifactName: "$flowArtifactoryKP", artifactVersion: "$BUILD_NUMBER", configuration: "$flowConfigName", filePath: "$artifactPath", overrideCredential: [credentialId: "$cred"], repositoryName: "$flowRepositoryName"
                }

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowRunPipeline') {
                    sh 'echo  =====================cloudBeesFlowRunPipeline====================='
                    cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"$flowPipelineName","parameters":[{"parameterName":"testParam1","parameterValue":""},{"parameterName":"TestParam2","parameterValue":""}]}}', configuration: "$flowConfigName", overrideCredential: [credentialId: "$cred"], pipelineName: "$flowPipelineName", projectName: "$flowProjectName"
                }

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowRunProcedure') {
                    sh 'echo  =====================cloudBeesFlowRunProcedure====================='
                    cloudBeesFlowRunProcedure configuration: "$flowConfigName", overrideCredential: [credentialId: "$cred"], procedureName: "$flowProcedureName", procedureParameters: '{"procedure":{"procedureName":"$flowProcedureName","parameters":[{"actualParameterName":"testParam1","value":""},{"actualParameterName":"testParam2","value":""}]}}', projectName: "$flowProjectName"
                }

                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowTriggerRelease') {
                    sh 'echo  =====================cloudBeesFlowTriggerRelease====================='
                    cloudBeesFlowTriggerRelease configuration: "$flowConfigName", overrideCredential: [credentialId: "$cred"], parameters: '{"release":{"releaseName":"pvRelease","stages":[{"stageName":"Stage 1","stageValue":""},{"stageName":"Stage 1 Copy 1","stageValue":""}],"pipelineName":"pipeline_pvRelease","parameters":[{"parameterName":"releaseTestParam1","parameterValue":""},{"parameterName":"releaseTestParam2","parameterValue":""}]}}', projectName: 'pvNativeJenkinsProject02', releaseName: "$flowReleaseName", startingStage: 'Stage 1 Copy 1'
                }
                if ("$runOnly" == '' || "$runOnly" == 'cloudBeesFlowCallRestApi') {
                    sh 'echo  =====================cloudBeesFlowCallRestApi====================='
                    cloudBeesFlowCallRestApi body: "$flowHTTPBody", configuration: "$flowConfigName", envVarNameForResult: "$flowEnvVarNameForResult", httpMethod: "$flowHTTPMethod", overrideCredential: [credentialId: "$cred"], urlPath: "$flowAPIURL"
                }
            }
        }
    }

}
