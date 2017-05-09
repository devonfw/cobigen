node {
    try {
		step([$class: 'WsCleanup'])
		stage('setting up environment & cloning repositories') { // for display purposes
			git credentialsId:'github-devonfw-ci', url:'https://github.com/devonfw/tools-cobigen.git'
			// Tools have to be configured in the global configuration of Jenkins.
			env.MAVEN_HOME="${tool 'Maven 3.3.9'}"
			env.JAVA_HOME="${tool 'OpenJDK 1.7'}"
			env.PATH="${env.MAVEN_HOME}/bin:${env.JAVA_HOME}/bin:${env.PATH}"
		}
		stage('build & test') {
			sh "mvn clean package -Pcobigen"
		}
		stage('process results') {
			archive '**/*.pdf'
			junit '**/target/*.xml'
		}
    } catch(e) {
        notifyFailed()
        throw e
    }
}

def notifyFailed() {

    step([$class: 'Mailer',
      notifyEveryUnstableBuild: true,
      recipients: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                      [$class: 'RequesterRecipientProvider']])])
    
    emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
         replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
         to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                 [$class: 'RequesterRecipientProvider']]))
}