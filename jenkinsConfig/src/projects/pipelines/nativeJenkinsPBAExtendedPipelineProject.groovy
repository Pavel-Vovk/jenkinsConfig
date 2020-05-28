package projects.pipelines
/*
parameters
buildParameters         -> Parameters for build gradlew                             -> default: 'clean build'
runOnly                 ->  Name of Run PBA, if '' - all PBAs                       -> default: ''
artifactPath            ->  Path to artifact on Jenkins side                        -> default: 'build/libs/gradle-test-build-4.9.jar'

flowConfigName          ->  CloudBees CD configuration name on Jenkins side         -> default: 'electricflow'
flowProjectName         ->  CloudBees CD Project Name                               -> default: 'pvNativeJenkinsProject01'
flowReleaseName         ->  CloudBees CD Release Name                               -> default: 'pvRelease'
flowApplication         ->  CloudBees CD Application Name                           -> default: 'pvNativeJenkinsTestApplication01'
flowApplicationProcess  ->  CloudBees CD Application Process Name                   -> default: 'pvDeployProcess'
flowEnvironmentName     ->  CloudBees CD Environment Name                           -> default: 'pvEnvironment'
flowArtifactoryKP       ->  CloudBees CD Artifact Group:Name                        -> default: 'pv:PBATests'
flowRepositoryName      ->  CloudBees CD target repository                          -> default: 'default'
flowPipelineName        ->  CloudBees CD Pipeline Name                              -> default: 'pvNativeJenkinsTestPipeline01'
flowProcedureName       ->  CloudBees CD Procedure Name                             -> default: 'nativeJenkinsTestProcedure'
flowStartingStage       ->  CloudBees CD Release Pipeline Stage                     -> default: 'Stage 1' (pvNativeJenkinsProject01 -> pvRelease -> 'Stage 1'; pvNativeJenkinsProject02 -> pvRelease -> 'Stage 1' or 'Stage 1 Copy 1')
flowRuntimeId           ->  CloudBees CD Pipeline Run FlowRuntimeId                 -> default: ''
flowHTTPBody            ->  CloudBees CD HTTP Body for API request                  -> default: ''
flowEnvVarNameForResult ->  CloudBees CD Variable name for saving the results       -> default: ''
flowHTTPMethod          ->  CloudBees CD HTTP method for API request                -> default: 'GET'
flowAPIURL              ->  CloudBees CD HTTP Url for API request                   -> default: '/projects'
*/
def jsonForReleaseParameters

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
                sh "./gradlew $buildParameters"
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

                if ("$runOnly" == '' || "$runOnly" =~ 'CreateAndDeployAppFromJenkinsPackage') {
                    sh 'echo  =====================cloudBeesFlowCreateAndDeployAppFromJenkinsPackage====================='
                    cloudBeesFlowCreateAndDeployAppFromJenkinsPackage configuration: "$flowConfigName", filePath: "$artifactPath"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'DeployApplication') {
                    sh 'echo  =====================cloudBeesFlowDeployApplication====================='
                    cloudBeesFlowDeployApplication applicationName: "$flowApplication", applicationProcessName: "$flowApplicationProcess", configuration: "$flowConfigName", deployParameters: '{"runProcess":{"applicationName":"$flowApplication","applicationProcessName":"$flowApplicationProcess","parameter":[{"actualParameterName":"deployTestAppParam1","value":""},{"actualParameterName":"deployTestAppParam2","value":""}]}}', environmentName: "$flowEnvironmentName", projectName: "$flowProjectName"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'PublishArtifact') {
                    sh 'echo  =====================cloudBeesFlowPublishArtifact====================='
                    cloudBeesFlowPublishArtifact artifactName: "$flowArtifactoryKP", artifactVersion: "$BUILD_NUMBER", configuration: "$flowConfigName", filePath: "$artifactPath", repositoryName: "$flowRepositoryName"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'RunPipeline') {
                    sh 'echo  =====================cloudBeesFlowRunPipeline====================='
                    cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"$flowPipelineName","parameters":[{"parameterName":"testParam1","parameterValue":""},{"parameterName":"TestParam2","parameterValue":""}]}}', configuration: "$flowConfigName", pipelineName: "$flowPipelineName", projectName: "$flowProjectName"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'RunProcedure') {
                    sh 'echo  =====================cloudBeesFlowRunProcedure====================='
                    cloudBeesFlowRunProcedure configuration: "$flowConfigName", procedureName: "$flowProcedureName", procedureParameters: '{"procedure":{"procedureName":"$flowProcedureName","parameters":[{"actualParameterName":"testParam1","value":""},{"actualParameterName":"testParam2","value":""}]}}', projectName: "$flowProjectName"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'TriggerRelease') {
                    sh 'echo  =====================TriggerRelease prepare JSON====================='
                    if ("$flowProjectName" == 'pvNativeJenkinsProject01') {
                        jsonForReleaseParameters = '{"release":{"releaseName":"pvRelease","stages":[{"stageName":"Stage 1","stageValue":""}],"pipelineName":"pipeline_pvRelease","parameters":[]}}'
                    }
                    if ("$flowProjectName" == 'pvNativeJenkinsProject02') {
                        jsonForReleaseParameters = '{"release":{"releaseName":"pvRelease","stages":[{"stageName":"Stage 1","stageValue":""},{"stageName":"Stage 1 Copy 1","stageValue":""}],"pipelineName":"pipeline_pvRelease","parameters":[{"parameterName":"releaseTestParam1","parameterValue":"releaseTestParam1Value"},{"parameterName":"releaseTestParam2","parameterValue":"releaseTestParam2Value"}]}}'
                    }
                    sh "echo Prepared JSON: $jsonForReleaseParameters"
                    sh 'echo  =====================cloudBeesFlowTriggerRelease====================='
                    cloudBeesFlowTriggerRelease configuration: "$flowConfigName", parameters: jsonForReleaseParameters, projectName: "$flowProjectName", releaseName: "$flowReleaseName", startingStage: "$flowStartingStage"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'CallRestApi') {
                    sh 'echo  =====================cloudBeesFlowCallRestApi====================='
                    cloudBeesFlowCallRestApi body: "$flowHTTPBody", configuration: "$flowConfigName", envVarNameForResult: "$flowEnvVarNameForResult", httpMethod: "$flowHTTPMethod", urlPath: "$flowAPIURL"
                }

                if ("$runOnly" == '' || "$runOnly" =~ 'AssociateBuildToRelease') {
                    sh 'echo  =====================cloudBeesFlowAssociateBuildToRelease====================='
                    cloudBeesFlowAssociateBuildToRelease configuration: "$flowConfigName", projectName: "$flowProjectName", releaseName: "$flowReleaseName", flowRuntimeid: "$flowRuntimeId"
                }
            }
        }
    }
}
