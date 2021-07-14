#!/bin/sh
set -e

if [ ! -f "output/Acknowledge-Document_cobigen.html" ]; then
	echo "File output/Acknowledge-Document_cobigen.html does not exist because solicitor.sh was not executed."
	echo "!!! Please MAKE SURE that the generated output/Acknowledge-Document_cobigen.html is legaly compliant before updating all license resources !!!"
	exit 1
fi

pushd "$(dirname ${BASH_SOURCE:0})"
trap popd EXIT

mvn -f license-to-txt-converter/ clean package
java -jar license-to-txt-converter/target/html2formattedtext-converter-jar-with-dependencies.jar output/Acknowledge-Document_cobigen.html output/Acknowledge-Document_cobigen.txt

cp output/Acknowledge-Document_cobigen.txt ../LICENSE-BUNDLE.txt
echo "updated ../LICENSE-BUNDLE.txt"

shopt -s nullglob globstar dotglob
for f in ../**/src/main/resources/META-INF/LICENSEP2BUNDLE.txt; do
  cp output/Acknowledge-Document_cobigen.txt $f
  echo "updated $f"
done

cp output/Acknowledge-Document_cobigen.txt ../cobigen-cli/cli/src/main/resources/META-INF/LICENSE.txt
echo "updated ../cobigen-cli/cli/src/main/resources/META-INF/LICENSE.txt"

cp output/Acknowledge-Document_cobigen.txt ../cobigen-eclipse/cobigen-eclipse/META-INF/LICENSE.txt
echo "updated ../cobigen-eclipse/cobigen-eclipse/META-INF/LICENSE.txt"

java -jar license-to-txt-converter/target/html2formattedtext-converter-jar-with-dependencies.jar -h output/Acknowledge-Document_cobigen.html output/Acknowledge-Document_cobigen-escaped.txt
LICENSE=`cat output/Acknowledge-Document_cobigen-escaped.txt`
ESCAPED_LICENSE=$(echo "${LICENSE}" | sed ':a;N;$!ba;s/\n/\\n/g' | sed 's/\$/\\$/g' | sed 's/>/\&gt;/g' | sed 's/</\&lt;/g' | sed 's/&/\\&/g')

# use for debugging the matching regex of the feature.xml
#echo `cat ../cobigen-eclipse/cobigen-eclipse-feature/feature.xml | sed -E -n '1h;1!H;$bend;b; :end g;s(<license[^>]+>)[^<]+(.+)\1'"${ESCAPED_LICENSE}"'\2p'`
sed -i -E -n '1h;1!H;$bend;b; :end g;s(<license[^>]+>)[^<]+(.+)\1'"${ESCAPED_LICENSE}"'\2p' ../cobigen-eclipse/cobigen-eclipse-feature/feature.xml\
 && echo "updated ../cobigen-eclipse/cobigen-eclipse-feature/feature.xml"\
 || echo "COULD NOT UPDATE ../cobigen-eclipse/cobigen-eclipse-feature/feature.xml"\
 && exit 1