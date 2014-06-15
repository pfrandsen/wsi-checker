pushd ..
mvn clean package
java -jar target/wsi-checker-1.0-SNAPSHOT.jar -unpackTool -root target/testrun/wsi-root
java -jar target/wsi-checker-1.0-SNAPSHOT.jar -generateConfig -root target/testrun/wsi-root -wsdl src/test/resources/wsdl/wsdl_1.wsdl -report target/testrun/report.xml -output target/testrun/config.xml -stylesheet wsi-root/common/xsl/report.xsl
java -jar target/wsi-checker-1.0-SNAPSHOT.jar -analyze -config target/testrun/config.xml -root target/testrun/wsi-root -summary target/testrun/summary.json
cd target/testrun
if python -c 'import sys; sys.exit(1 if sys.hexversion<0x03000000 else 0)'
then
    python -m http.server & # Python 3.x
else
    python -m SimpleHTTPServer & # Python 2.x
fi
google-chrome http://localhost:8000/report.xml
popd
