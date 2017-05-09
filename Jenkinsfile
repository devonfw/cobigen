node {
    try {
		
		stage('prepare') {
			env.GIT_COMMIT = sh(script: "git rev-parse HEAD", returnStdout: true).trim()
			setBuildStatus("In Progress","PENDING")
			step([$class: 'WsCleanup'])
		}
		
		stage('setting up environment & cloning repositories') { // for display purposes
			git credentialsId:'github-devonfw-ci', url:'https://github.com/devonfw/tools-cobigen.git', branch: "${env.BRANCH_NAME}"
			// Tools have to be configured in the global configuration of Jenkins.
			env.MAVEN_HOME="${tool 'Maven 3.3.9'}"
			if (env.BRANCH_NAME == "dev_mavenplugin") {
				env.JAVA_HOME="${tool 'OpenJDK 1.8'}"
			} else {
				env.JAVA_HOME="${tool 'OpenJDK 1.7'}"
			}
			env.PATH="${env.MAVEN_HOME}/bin:${env.JAVA_HOME}/bin:${env.PATH}"
			tool 'VNC Server'
		}
		
		def non_deployable_branches = ["master","gh-pages","dev_eclipseplugin","dev_oomph_setup"]
		def root = ""
		if (env.BRANCH_NAME == "master") {
			root = ""
		} else if (env.BRANCH_NAME == "dev_eclipseplugin") {
			root = "cobigen-eclipse"
		} else if (env.BRANCH_NAME == "dev_htmlmerger") {
			root = "cobigen/cobigen-htmlplugin"
		} else if (env.BRANCH_NAME == "dev_mavenplugin") {
			root = "cobigen-maven"
		} else if (env.BRANCH_NAME == "dev_tempeng_freemarker") {
			root = "cobigen/cobigen-templateengines/cobigen-tempeng-freemarker"
		} else if (env.BRANCH_NAME == "dev_core") {
			root = "cobigen/cobigen-core-parent"
		} else if (env.BRANCH_NAME == "gh-pages" || env.BRANCH_NAME == "dev_oomph_setup") {
			currentBuild.result = 'SUCCESS'
			setBuildStatus("Complete","SUCCESS")
			sh "exit 0"
		} else {
			root = "cobigen/cobigen-" + env.BRANCH_NAME.replace("dev_", "")
		}
		
		stage('build & test') {
			dir(root) {
				// https://github.com/jenkinsci/xvnc-plugin/blob/master/src/main/java/hudson/plugins/xvnc/Xvnc.java
				wrap([$class:'Xvnc', useXauthority: true]) { // takeScreenshot: true, causes issues seemingly
					// just skip tycho tests (running in integration-test phase) as they are not yet working due to xvnc issues
					// current warning, which maybe points to the cause: 
					// Xlib:  extension "RANDR" missing on display
					// waiting for https://github.com/jenkinsci/xvnc-plugin/pull/12 to add necessary +extension RANDR command
					sh "mvn clean package"
				}
			}
		}
		
		stage('process test results') {
			step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/*.xml'])
			if (currentBuild.result == 'UNSTABLE') {
				setBuildStatus("Complete","FAILURE")
			}
		}
		
		stage('deploy') {
			dir(root) {
				if (!non_deployable_branches.contains(env.BRANCH_NAME)) {
					sh "mvn deploy -Dmaven.test.skip=true"
				}
			}
		}
		
    } catch(e) {
		notifyFailed()
		if (currentBuild.result != 'UNSTABLE') {
			setBuildStatus("Incomplete","ERROR")
		}
        throw e
    }
	 setBuildStatus("Complete","SUCCESS")
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

def setBuildStatus(String message, String state) {
	githubNotify context: "Jenkins-Tests", description: message, status: state, credentialsId: 'github-devonfw-ci', sha: "${GIT_COMMIT}", targetUrl: "${BUILD_URL}", account: 'devonfw', repo: 'tools-cobigen'
}