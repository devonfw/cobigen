cd cobigen-eclipse
call mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=%1
cd ..
cd cobigen-eclipse-test
call mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=%1
cd ..
cd cobigen-eclipse-feature
call mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=%1
cd ..
cd cobigen-eclipse-updatesite
call mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=%1
cd ..
pause