set -e

#clean
rm -Rf output/
rm -Rf target/solicitor
mkdir -p target/solicitor

# does not work correctly, generates wrong license files if executed on any parent
#<plugin>
#        <groupId>org.codehaus.mojo</groupId>
#        <artifactId>license-maven-plugin</artifactId>
#        <version>2.0.0</version>
#        <executions>
#          <execution>
#            <id>generate-license-report</id>
#            <phase>prepare-package</phase>
#            <goals>
#              <goal>aggregate-download-licenses</goal>
#            </goals>
#            <inherited>true</inherited>
#            <configuration>
#              <sortArtifactByName>true</sortArtifactByName>
#              <includeTransitiveDependencies>true</includeTransitiveDependencies>
#              <failOnMissing>false</failOnMissing>
#              <encoding>utf-8</encoding>
#              <executeOnlyOnRootModule>false</executeOnlyOnRootModule>
#              <offline>true</offline>
#              <excludedGroups>com.devonfw.cobigen -P!p2-build -Dsettings.offline=true</excludedGroups>
#              <excludedScopes>test,provided</excludedScopes>
#            </configuration>
#          </execution>
#        </executions>
#      </plugin>

cd cobigen-eclipse/cobigen-eclipse
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen,p2.eclipse-plugin -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-cli/cli
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-maven/cobigen-maven-plugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-htmlplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-javaplugin-parent/cobigen-javaplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-jsonplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-openapiplugin-parent/cobigen-openapiplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-propertyplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-templateengines/cobigen-tempeng-freemarker
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-templateengines/cobigen-tempeng-velocity
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-textmerger
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-tsplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-xmlplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.excludedScopes=test,provided -Dlicense.excludedGroups=com.devonfw.cobigen -P!p2-build -Dsettings.offline=true
cd ../..

cp cobigen-eclipse/cobigen-eclipse/target/generated-resources/licenses.xml target/solicitor/licenses_eclipse.xml
cp cobigen-cli/cli/target/generated-resources/licenses.xml target/solicitor/licenses_cli.xml
cp cobigen-maven/cobigen-maven-plugin/target/generated-resources/licenses.xml target/solicitor/licenses_maven.xml
cp cobigen-plugins/cobigen-htmlplugin/target/generated-resources/licenses.xml target/solicitor/licenses_htmlplugin.xml
cp cobigen-plugins/cobigen-javaplugin-parent/cobigen-javaplugin/target/generated-resources/licenses.xml target/solicitor/licenses_javaplugin.xml
cp cobigen-plugins/cobigen-jsonplugin/target/generated-resources/licenses.xml target/solicitor/licenses_jsonplugin.xml
cp cobigen-plugins/cobigen-openapiplugin-parent/cobigen-openapiplugin/target/generated-resources/licenses.xml target/solicitor/licenses_openapiplugin.xml
cp cobigen-plugins/cobigen-propertyplugin/target/generated-resources/licenses.xml target/solicitor/licenses_propertyplugin.xml
cp cobigen-plugins/cobigen-templateengines/cobigen-tempeng-freemarker/target/generated-resources/licenses.xml target/solicitor/licenses_tempeng_freemarker.xml
cp cobigen-plugins/cobigen-templateengines/cobigen-tempeng-velocity/target/generated-resources/licenses.xml target/solicitor/licenses_tempeng_velocity.xml
cp cobigen-plugins/cobigen-textmerger/target/generated-resources/licenses.xml target/solicitor/licenses_textmerger.xml
cp cobigen-plugins/cobigen-tsplugin/target/generated-resources/licenses.xml target/solicitor/licenses_tsplugin.xml
cp cobigen-plugins/cobigen-xmlplugin/target/generated-resources/licenses.xml target/solicitor/licenses_xmlplugin.xml

if [ ! -f "../solicitor.jar" ]; then
	wget -O ../solicitor.jar https://github.com/devonfw/solicitor/releases/download/v1.2.0/solicitor.jar
fi

cd target/solicitor

if [ -n "$1" ]; then
	java -Dloader.path=../../$1 -jar ../../../solicitor.jar -c file:../../solicitor.cfg
else
	java -jar ../../../solicitor.jar -c file:../../solicitor.cfg
fi

cd ../..
