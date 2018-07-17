properties([
  parameters([
    string(name: 'TRIGGER_SHA', defaultValue: '', description: 'The sha of the commit that triggered the calling job'),
    string(name: 'TRIGGER_REPO', defaultValue: '', description: 'The URI of the commit that triggered the calling job')
   ]),
   [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10']]
])
node {
    //lock(resource: "pipeline_${env.NODE_NAME}_${env.JOB_NAME}", inversePrecedence: false) {
		try {	
			stage('prepare') {
				step([$class: 'WsCleanup'])
			}

			// will hold the current branch name
			def origin_branch =""
			
			stage('setting up environment & cloning repositories') {
				checkout scm

				// CHANGE_TARGET seems only to be set at PR- build jobs. Since there is (seemingly) no documentation the variable is used if it is present. Otherwise BRANCH_NAME is used that fits on normal branch builds
				if(env.CHANGE_TARGET != null){
					origin_branch = env.CHANGE_TARGET
					echo 'using CHANGE_TARGET as branch name: '+origin_branch
				}else{
					origin_branch=env.BRANCH_NAME
					echo 'using BRANCH_NAME as branch name: '+origin_branch 
				}

				env.GIT_COMMIT = sh(script: "git rev-parse HEAD", returnStdout: true).trim()
				setBuildStatus("In Progress","PENDING")
			
				// Tools have to be configured in the global configuration of Jenkins.
				env.MAVEN_HOME="${tool 'Maven 3.3.9'}"
				env.M2_HOME="${env.MAVEN_HOME}" // for recognition by maven invoker (test utility)
				env.JAVA_HOME="${tool 'OpenJDK 1.8'}"
				env.PATH="${env.MAVEN_HOME}/bin:${env.JAVA_HOME}/bin:${env.PATH}"
				// load VNC Server for eclipse tests
				tool 'VNC Server'
			}
			
			def non_deployable_branches = ["master","gh-pages","dev_eclipseplugin","dev_oomph_setup"]
			def root = ""
			if (origin_branch == "master") {
				if(justTemplatesChanged()) {
					echo "Just Templates changed!"
					root = "cobigen-templates"
				} else {
					root = ""
				}
			} else if (origin_branch == "dev_eclipseplugin") {
				root = "cobigen-eclipse"
			} else if (origin_branch == "dev_htmlmerger") {
				root = "cobigen/cobigen-htmlplugin"
			} else if (origin_branch == "dev_mavenplugin") {
				root = "cobigen-maven"
			} else if (origin_branch == "dev_tempeng_freemarker") {
				root = "cobigen/cobigen-templateengines/cobigen-tempeng-freemarker"
			} else if (origin_branch == "dev_tempeng_velocity") {
				root = "cobigen/cobigen-templateengines/cobigen-tempeng-velocity"
			} else if (origin_branch == "dev_core") {
				root = "cobigen/cobigen-core-parent"
			} else if (origin_branch == "dev_javaplugin") {
				root = "cobigen/cobigen-javaplugin-parent"
			} else if (origin_branch == "dev_jssenchaplugin") {
				root = "cobigen/cobigen-senchaplugin"
			} else if (origin_branch == "gh-pages" || origin_branch == "dev_oomph_setup") {
				currentBuild.result = 'SUCCESS'
				setBuildStatus("Complete","SUCCESS")
				sh "exit 0"
			} else {
				root = "cobigen/cobigen-" + origin_branch.replace("dev_", "")
			}
			
			stage('build & test') {
				dir(root) {
					// https://github.com/jenkinsci/xvnc-plugin/blob/master/src/main/java/hudson/plugins/xvnc/Xvnc.java
					wrap([$class:'Xvnc', useXauthority: true]) { // takeScreenshot: true, causes issues seemingly
						withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'pl-technical-user', usernameVariable: 'DEVON_NEXUS_USER', passwordVariable: 'DEVON_NEXUS_PASSWD']]) {
						
							// load jenkins managed global maven settings file
							configFileProvider([configFile(fileId: '9d437f6e-46e7-4a11-a8d1-2f0055f14033', variable: 'MAVEN_SETTINGS')]) {
								try {
									if(origin_branch == 'master') {
										sh "mvn -s ${MAVEN_SETTINGS} clean install -Pp2-build-mars,p2-build-stable"
									} else {
										sh "mvn -s ${MAVEN_SETTINGS} clean install -Pp2-build-mars,p2-build-ci"
									}
								} catch(err) {
									step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: false])
									if (currentBuild.result != 'UNSTABLE') { // JUnitResultArchiver sets result to UNSTABLE. If so, indicate UNSTABLE, otherwise throw error.
										throw err
									}
								}
							}
						}
					}
				}
			}
			
			if (currentBuild.result == 'UNSTABLE') {
				setBuildStatus("Complete","FAILURE")
				notifyFailed()
				return // do the return outside of stage area to exit the pipeline
			}
			
			stage('process test results') {
				// added 'allowEmptyResults:true' to prevent failure in case of no tests
				step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true])
			}
			
			if (currentBuild.result == 'UNSTABLE') {
				setBuildStatus("Complete","FAILURE")
				notifyFailed()
				return
			}
			
			stage('deploy') {
				dir(root) {
					configFileProvider([configFile(fileId: '9d437f6e-46e7-4a11-a8d1-2f0055f14033', variable: 'MAVEN_SETTINGS')]) {
						if (!non_deployable_branches.contains(origin_branch)) {
							sh "mvn -s ${MAVEN_SETTINGS} deploy -Dmaven.test.skip=true"
							
							if (origin_branch != 'dev_core' && origin_branch != 'dev_mavenplugin'){
								def deployRoot = ""
								if(origin_branch == 'dev_javaplugin'){
									deployRoot = "cobigen-javaplugin"
								}
								dir(deployRoot) {
									// we currently need these three steps to assure the correct sequence of packaging,
									// manifest extension, osgi bundling, and upload
								sh "mvn -s ${MAVEN_SETTINGS} package bundle:bundle -Pp2-bundle,p2-build-mars,p2-build-ci -Dmaven.test.skip=true"
								sh "mvn -s ${MAVEN_SETTINGS} install bundle:bundle -Pp2-bundle,p2-build-mars,p2-build-ci p2:site -Dmaven.test.skip=true"
								sh "mvn -s ${MAVEN_SETTINGS} deploy -Pp2-build-mars,p2-build-ci -Dmaven.test.skip=true -Dp2.upload=ci"
								}
							}
						} else if(origin_branch == 'dev_eclipseplugin') {
							withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'fileserver', usernameVariable: 'ICSD_FILESERVER_USER', passwordVariable: 'ICSD_FILESERVER_PASSWD']]) {
								sh "mvn -s ${MAVEN_SETTINGS} deploy -Dmaven.test.skip=true -Pp2-build-mars,p2-build-ci -Dp2.upload=ci"
							}
						}
					}
				}
			}

			if(origin_branch != 'dev_eclipseplugin' && origin_branch != 'dev_mavenplugin' && origin_branch != 'master'){
				stage('integration-test') {
					def repo = sh(script: "git config --get remote.origin.url", returnStdout: true).trim()
					build job: 'dev_eclipseplugin', wait: false, parameters: [[$class:'StringParameterValue', name:'TRIGGER_SHA', value:env.GIT_COMMIT], [$class:'StringParameterValue', name:'TRIGGER_REPO', value: repo]]
					build job: 'dev_mavenplugin', wait: false, parameters: [[$class:'StringParameterValue', name:'TRIGGER_SHA', value:env.GIT_COMMIT], [$class:'StringParameterValue', name:'TRIGGER_REPO', value: repo]]
				}
			}

		} catch(e) {
			if (currentBuild.result != 'UNSTABLE') {
			  currentBuild.result = 'FAILURE'
				setBuildStatus("Incomplete","ERROR")
			}
			notifyFailed()
			return
		}
		setBuildStatus("Complete","SUCCESS")
	//}
}

def isPRBuild() {
    return (env.BRANCH_NAME ==~ /^PR-\d+$/)
}

def justTemplatesChanged() {
	// split will return a list with one element (the empty string) if called on an empty string
	diff_files= sh(script: "git diff --name-only origin/master | xargs", returnStdout: true).trim().split("\\s+")
	for(int i=0; i < diff_files.size(); i++) {
		if(!diff_files[i].startsWith("cobigen-templates/")) {
			echo "'${diff_files[i]}' does not start with cobigen-templates/"
			return false
		}
	}
	return true
}

def notifyFailed() {
    
    emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
         replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
         to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                 [$class: 'RequesterRecipientProvider'],
				 [$class: 'DevelopersRecipientProvider'],
				 [$class: 'FailingTestSuspectsRecipientProvider'],
				 [$class: 'UpstreamComitterRecipientProvider']]))
}

def setBuildStatus(String message, String state) {
	try{
		if(env.TRIGGER_SHA != null && env.TRIGGER_SHA != '' && env.TRIGGER_REPO != null && TRIGGER_REPO != '') {
			step([$class: 'GitHubCommitStatusSetter', commitShaSource: [$class:'ManuallyEnteredShaSource', sha:env.TRIGGER], reposSource: [$class:'ManuallyEnteredRepositorySource', url:env.TRIGGER_REPO], contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: "integration-test"], statusResultSource: [$class: 'ConditionalStatusResultSource', results: [[$class: 'AnyBuildResult', message: message, state: state]]]])
		}
	} catch(e) {
		echo "Could not set build status for ${params.TRIGGER}: ${message}, ${state}"
		echo "Exception ${e.toString()}:${e.getMessage()}"
	}
}
