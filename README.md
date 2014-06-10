# wsi-checker

Utility for running WS-I.org Basic Profile 1.1 validation tool

## Build/test

### Tool to analyze wsdl

* To build stand alone jar: mvn package
* Run: java -jar target/wsi-checker-1.0-SNAPSHOT.jar -config file  [-summary file]

Examples:

* java -jar target/wsi-checker-1.0-SNAPSHOT.jar -config target/config.xml
* java -jar target/wsi-checker-1.0-SNAPSHOT.jar -config target/config.xml -summary target/summary.json


### Tool to generate wsi configuration file

* To build stand alone jar: mvn -Pconfig package
* Run: java -jar target/config-generator.jar -wsdl file -report file -output file
* Run: java -jar target/config-generator.jar -wsdl file -report file -output file -binding index

Example: java -jar target/config-generator.jar -wsdl src/test/resources/wsdl/wsdl_1.wsdl -report target/report.xml
 -output target/config.xml

Default is to use the first binding in the wsdl. If the wsdl has more than one binding, add the -binding option to
generate config file for a specific binding.

## Content

### src/main/java

* WsiBasicProfileChecker Generate config file, run validator, analyze result (see tests for example usage).
* ReportParser Simple DOM parser for extracting information from the report file generated by the validator tool.
* Util Utility functions to access bindings in wsdl, read template file, etc.
* check/. Classes used to collect error/warning information.
* wsi_binding_config_template.xml Template used to create a config file that the ws-i tool need as input.

### WS-I

The WS-I folder contains the ws-i.org basic profile libraries, profiles schemas etc. The java/lib folder has
been cleaned up to just contain the libraries (wsi-test-tools.jar, wsdl4j.jar, uddi4j.jar) and xerces has been
moved to the pom. These are the jar files that this project need for the WSDL validation being performed. Other
validations may need more of the libraries from the standard tool distribution.

The libraries have also been installed in the local maven repository (WS-I/repository) to enable building stand
alone (executable) jars.

#### Example: Running the analyzer from a Java program

``` java
    public runner() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", "target/wsi-checker-1.0-SNAPSHOT.jar",
                "-config", "target/config.xml", "-summary", "target/summary.json");
        Process process = builder.start();
        InputStream fromProcess = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(fromProcess));
        String line;
        boolean success = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("WSDL analysis completed with status: SUCCESS")) {
                success = true;
            }
        }
        System.out.println(success ? "Success" : "failure");
    }
```
