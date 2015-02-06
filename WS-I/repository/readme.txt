
The wsi jars are installed in this local repository using these commands:

mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/axis.jar -DgroupId=wsi -DartifactId=axis -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/commons-discovery.jar -DgroupId=wsi -DartifactId=commons-discovery -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/commons-logging.jar -DgroupId=wsi -DartifactId=commons-logging -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/jaxrpc.jar -DgroupId=wsi -DartifactId=jaxrpc -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/saaj.jar -DgroupId=wsi -DartifactId=saaj -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/uddi4j.jar -DgroupId=wsi -DartifactId=uddi4j -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/wsdl4j.jar -DgroupId=wsi -DartifactId=wsdl4j -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/wsi-test-tools.jar -DgroupId=wsi -DartifactId=wsi-test-tools -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/xercesImpl.jar -DgroupId=wsi -DartifactId=xercesImpl -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
mvn install:install-file -Dfile=WS-I/wsi-test-tools/java/lib/xmlParserAPIs.jar -DgroupId=wsi -DartifactId=xmlParserAPIs -Dversion=local -Dpackaging=jar -DlocalRepositoryPath=WS-I/repository
