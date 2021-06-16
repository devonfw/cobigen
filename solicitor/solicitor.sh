set -e

solicitor_version=v1.2.0

bash_source_dir=`dirname $BASH_SOURCE`
exec_path=`pwd`
echo "**********************************************"
echo "Running Solicitor of version $solicitor_version"
if [ -n "$1" ]; then
    echo "Considering extension $exec_path/$1"
else
    echo "No extension passed as first argument"
fi
echo "**********************************************"

cd $bash_source_dir

#clean
rm -Rf output/
rm -Rf input/
mkdir -p input/

filter_file=`realpath artifacts-filter.txt`
filter_file=file:///`echo "$filter_file" | sed 's/^\///' | sed 's/^./\0:/'`
#echo $filter_file

cd ../cobigen-eclipse/cobigen-eclipse
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-cli/cli
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-maven/cobigen-maven-plugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-htmlplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-javaplugin-parent/cobigen-javaplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-jsonplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-openapiplugin-parent/cobigen-openapiplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-propertyplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-templateengines/cobigen-tempeng-freemarker
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-templateengines/cobigen-tempeng-velocity
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../../..
cd cobigen-plugins/cobigen-textmerger
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-tsplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..
cd cobigen-plugins/cobigen-xmlplugin
mvn org.codehaus.mojo:license-maven-plugin:2.0.0:aggregate-download-licenses -Dlicense.artifactFiltersUrl=$filter_file -P!p2-build -Dsettings.offline=true
cd ../..

cp cobigen-eclipse/cobigen-eclipse/target/generated-resources/licenses.xml solicitor/input/licenses_eclipse.xml
cp cobigen-cli/cli/target/generated-resources/licenses.xml solicitor/input/licenses_cli.xml
cp cobigen-maven/cobigen-maven-plugin/target/generated-resources/licenses.xml solicitor/input/licenses_maven.xml
cp cobigen-plugins/cobigen-htmlplugin/target/generated-resources/licenses.xml solicitor/input/licenses_htmlplugin.xml
cp cobigen-plugins/cobigen-javaplugin-parent/cobigen-javaplugin/target/generated-resources/licenses.xml solicitor/input/licenses_javaplugin.xml
cp cobigen-plugins/cobigen-jsonplugin/target/generated-resources/licenses.xml solicitor/input/licenses_jsonplugin.xml
cp cobigen-plugins/cobigen-openapiplugin-parent/cobigen-openapiplugin/target/generated-resources/licenses.xml solicitor/input/licenses_openapiplugin.xml
cp cobigen-plugins/cobigen-propertyplugin/target/generated-resources/licenses.xml solicitor/input/licenses_propertyplugin.xml
cp cobigen-plugins/cobigen-templateengines/cobigen-tempeng-freemarker/target/generated-resources/licenses.xml solicitor/input/licenses_tempeng_freemarker.xml
cp cobigen-plugins/cobigen-templateengines/cobigen-tempeng-velocity/target/generated-resources/licenses.xml solicitor/input/licenses_tempeng_velocity.xml
cp cobigen-plugins/cobigen-textmerger/target/generated-resources/licenses.xml solicitor/input/licenses_textmerger.xml
cp cobigen-plugins/cobigen-tsplugin/target/generated-resources/licenses.xml solicitor/input/licenses_tsplugin.xml
cp cobigen-plugins/cobigen-xmlplugin/target/generated-resources/licenses.xml solicitor/input/licenses_xmlplugin.xml

cd solicitor/

if [ ! -f "solicitor-$solicitor_version.jar" ]; then
	curl -OL https://github.com/devonfw/solicitor/releases/download/$solicitor_version/solicitor.jar
	mv solicitor.jar solicitor-$solicitor_version.jar
fi

if [ -n "$1" ]; then
	java -Dloader.path=$exec_path/$1 -jar solicitor-$solicitor_version.jar -c file:solicitor.cfg
else
	java -jar solicitor-$solicitor_version.jar -c file:solicitor.cfg
fi

