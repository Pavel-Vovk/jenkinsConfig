// imports
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins

//define all creds
def jenkinsKeyUsernameWithPasswordParameters = [
        description:  'Flow Admin User',
        id:           '4',
        secret:       'changeme',
        userName:     'admin'
]
def credentials = []
credentials.add(jenkinsKeyUsernameWithPasswordParameters)

jenkinsKeyUsernameWithPasswordParameters = [
        description:  'Flow user1. User has access to pvNativeJenkinsProject01, Default, EC-Utilities',
        id:           '1',
        secret:       'changeme',
        userName:     'user1'
]
credentials.add(jenkinsKeyUsernameWithPasswordParameters)

jenkinsKeyUsernameWithPasswordParameters = [
        description:  'Flow user2. User has access to pvNativeJenkinsProject02, Default, EC-Utilities',
        id:           '2',
        secret:       'changeme',
        userName:     'user2'
]
credentials.add(jenkinsKeyUsernameWithPasswordParameters)

jenkinsKeyUsernameWithPasswordParameters = [
        description:  'Flow slave. User has access to Default, EC-Utilities',
        id:           '3',
        secret:       'changeme',
        userName:     'slave'
]
credentials.add(jenkinsKeyUsernameWithPasswordParameters)

// parameters


// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// get credentials domain
def domain = Domain.global()

// get credentials store
def store = jenkins.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// define Bitbucket secret
for (credential in credentials){
    def jenkinsKeyUsernameWithPassword = new UsernamePasswordCredentialsImpl(
            CredentialsScope.GLOBAL,
            credential.id,
            credential.description,
            credential.userName,
            credential.secret
    )

// add credential to store
   store.addCredentials(domain, jenkinsKeyUsernameWithPassword)
}

// save to disk
jenkins.save()