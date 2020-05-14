package projects.pipelines
/*
parameters
runOnly                 ->  Name of Run PBA, if '' - all PBAs
artifactPath            ->  Path to artifact on Jenkins side
cred                    ->  user Credential Id
flowConfigName          ->  CloudBees Flow configuration name on Jenkins side
flowProjectName         ->  CloudBees Flow Project Name
flowReleaseName         ->  CloudBees Flow Release Name
flowApplication         ->  CloudBees Flow Application Name
flowApplicationProcess  ->  CloudBees Flow Application Process Name
flowEnvironmentName     ->  CloudBees Flow Environment Name
flowArtifactoryKP       ->  CloudBees Flow Artifact Group:Name
flowRepositoryName      ->  CloudBees Flow target repository
flowPipelineName        ->  CloudBees Flow Pipeline Name
flowProcedureName       ->  CloudBees Flow Procedure Name
flowHTTPBody            ->  CloudBees Flow HTTP Body for API request
flowEnvVarNameForResult ->  CloudBees Flow Variable name for saving the results
flowHTTPMethod          ->  CloudBees Flow HTTP method for API request
flowAPIURL              ->  CloudBees Flow HTTP Url for API request
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
                sh 'echo "=================== Post Build Actions ===================="'
            }
        }
    }
    post {
        always {
            script {
                sh 'echo =====================archiveArtifacts====================='
                archiveArtifacts 'target/*.jar'

                sh 'echo =====================JUnit====================='
                junit 'target/surefire-reports/*.xml'

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
