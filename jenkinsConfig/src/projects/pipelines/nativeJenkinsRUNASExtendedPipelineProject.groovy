package projects.pipelines

pipeline{
    agent any
    stages {
        stage('first step') {
            steps {
                sh 'echo "=================== Run on jenkins side ===================="'
                sh 'echo `pwd`'
                sh 'echo `whoami`'
                sh 'echo `uname -a`'
                sh 'echo `hostname`'
                sh 'echo "=================== Post Build Actions ===================="'
                sh 'echo "====== cloudBeesFlowDeployApplication ======"'
                sh 'echo "------ cloudBeesFlowDeployApplication ------"'
                cloudBeesFlowDeployApplication applicationName: 'pvNativeJenkinsTestApplication01', applicationProcessName: 'pvDeployProcess', configuration: 'electricflow', deployParameters: '{"runProcess":{"applicationName":"pvNativeJenkinsTestApplication01","applicationProcessName":"pvDeployProcess","parameter":[]}}', environmentName: 'pvEnvironment', overrideCredential: [credentialId: "$cred"], projectName: 'pvNativeJenkinsProject01'


                sh 'echo "====== cloudBeesFlowRunPipeline ======"'
                sh 'echo "------ cloudBeesFlowRunPipeline ------ user1 ------"'
                //new
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline01","parameters":[{"parameterName":"testParam1","parameterValue":""},{"parameterName":"TestParam2","parameterValue":""}]}}', configuration: 'electricflow', overrideCredential: [credentialId: '1'], pipelineName: 'pvNativeJenkinsTestPipeline01', projectName: 'pvNativeJenkinsProject01'
                //old
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline01","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '1'], pipelineName: 'pvNativeJenkinsTestPipeline01', projectName: 'pvNativeJenkinsProject01'
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline02","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '1'], pipelineName: 'pvNativeJenkinsTestPipeline02', projectName: 'pvNativeJenkinsProject01'

                sh 'echo "------ cloudBeesFlowRunPipeline ------ user2 ------"'
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline01","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '2'], pipelineName: 'pvNativeJenkinsTestPipeline01', projectName: 'pvNativeJenkinsProject02'
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline02","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '2'], pipelineName: 'pvNativeJenkinsTestPipeline02', projectName: 'pvNativeJenkinsProject02'

                sh 'echo "------ cloudBeesFlowRunPipeline ------ admin ------"'
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline01","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '4'], pipelineName: 'pvNativeJenkinsTestPipeline01', projectName: 'pvNativeJenkinsProject01'
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline02","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '4'], pipelineName: 'pvNativeJenkinsTestPipeline02', projectName: 'pvNativeJenkinsProject01'
                cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline01","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '4'], pipelineName: 'pvNativeJenkinsTestPipeline01', projectName: 'pvNativeJenkinsProject02'                //cloudBeesFlowRunPipeline addParam: '{"pipeline":{"pipelineName":"pvNativeJenkinsTestPipeline02","parameters":[]}}', configuration: 'electricflow', overrideCredential: [credentialId: '4'], pipelineName: 'pvNativeJenkinsTestPipeline02', projectName: 'pvNativeJenkinsProject02'


                sh 'echo "====== cloudBeesFlowRunProcedure ======"'
                sh 'echo "------ cloudBeesFlowRunProcedure ------ user1 ------"'
                cloudBeesFlowRunProcedure configuration: 'electricflow', overrideCredential: [credentialId: '1'], procedureName: 'nativeJenkinsTestProcedure', procedureParameters: '{"procedure":{"procedureName":"nativeJenkinsTestProcedure","parameters":[]}}', projectName: 'pvNativeJenkinsProject01'


                sh 'echo "------ cloudBeesFlowRunProcedure ------ user2 ------"'
                cloudBeesFlowRunProcedure configuration: 'electricflow', overrideCredential: [credentialId: '2'], procedureName: 'nativeJenkinsTestProcedure', procedureParameters: '{"procedure":{"procedureName":"nativeJenkinsTestProcedure","parameters":[]}}', projectName: 'pvNativeJenkinsProject02'

                sh 'echo "------ cloudBeesFlowRunProcedure ------ admin ------"'
                cloudBeesFlowRunProcedure configuration: 'electricflow', overrideCredential: [credentialId: '4'], procedureName: 'nativeJenkinsTestProcedure', procedureParameters: '{"procedure":{"procedureName":"nativeJenkinsTestProcedure","parameters":[]}}', projectName: 'pvNativeJenkinsProject01'
                cloudBeesFlowRunProcedure configuration: 'electricflow', overrideCredential: [credentialId: '4'], procedureName: 'nativeJenkinsTestProcedure', procedureParameters: '{"procedure":{"procedureName":"nativeJenkinsTestProcedure","parameters":[]}}', projectName: 'pvNativeJenkinsProject02'

                sh 'echo "------ cloudBeesFlowRunProcedure ------ Parameter CRED ------"'
                //cloudBeesFlowRunProcedure configuration: 'electricflow', overrideCredential: [credentialId: 'CRED'], procedureName: 'nativeJenkinsTestProcedure', procedureParameters: '{"procedure":{"procedureName":"nativeJenkinsTestProcedure","parameters":[]}}', projectName: 'pvNativeJenkinsProject02'
                sh 'echo "====== cloudBeesFlowCallRestApi ======"'
                sh 'echo "------ cloudBeesFlowCallRestApi ------ user1 ------"'
                cloudBeesFlowCallRestApi body: '', configuration: 'electricflow', envVarNameForResult: '', httpMethod: 'GET', overrideCredential: [credentialId: "$CRED"], urlPath: '/projects'

                sh 'echo "------ cloudBeesFlowCallRestApi ------ user2 ------"'
                cloudBeesFlowCallRestApi body: '', configuration: 'electricflow', envVarNameForResult: '', httpMethod: 'GET', overrideCredential: [credentialId: '2'], urlPath: '/projects'

                sh 'echo "------ cloudBeesFlowCallRestApi ------ slave ------"'
                cloudBeesFlowCallRestApi body: '', configuration: 'electricflow', envVarNameForResult: '', httpMethod: 'GET', overrideCredential: [credentialId: '3'], urlPath: '/projects'

                sh 'echo "------ cloudBeesFlowCallRestApi ------ admin ------"'
                cloudBeesFlowCallRestApi body: '', configuration: 'electricflow', envVarNameForResult: '', httpMethod: 'GET', overrideCredential: [credentialId: '4'], urlPath: '/projects'

                //sh 'echo "=================== End run on jenkins side ==================="'
            }
        }
    }
}