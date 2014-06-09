
The wsi jars are installed in this local repository using these commands:

mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/wsdl4j.jar -DgroupId=wsi -DartifactId=wsdl4j -Dversion=1.4 -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/uddi4j.jar -DgroupId=wsi -DartifactId=uddi4j -Dversion=unknown -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/wsi-test-tools.jar -DgroupId=wsi -DartifactId=wsi-test-tools -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
