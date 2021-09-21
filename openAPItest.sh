#!/bin/sh
mvn install -f cobigen-eclipse -Pp2-build -Dtest=OpenAPITest#testRegexBasedOpenAPIGeneration

