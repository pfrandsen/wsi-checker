package dk.pfrandsen;

import dk.pfrandsen.check.AnalysisInformation;
import dk.pfrandsen.check.AnalysisInformationCollector;
import dk.pfrandsen.wsdl.*;
import dk.pfrandsen.wsdl.wsi.WsiBasicProfileChecker;
import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// TODO: Add options for specifying WS-I tools root and location of report stylesheet file

public class AnalyzeWsdl {
    static private String OPTION_HELP = "help";
    static private String OPTION_CONFIG = "config";
    static private String OPTION_SUMMARY = "summary";
    static private String USAGE = "Usage: java -jar <jar-file> -" + OPTION_CONFIG + " <file> " +
            " [-" + OPTION_SUMMARY + " <file>]";

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, getCommandlineOptions());
    }

    private static Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(OPTION_HELP, USAGE);

        Option config = OptionBuilder.withArgName("file")
                .hasArg()
                .isRequired()
                .withDescription("location of configuration file")
                .create(OPTION_CONFIG);
        Option summary = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("location of summary file")
                .create(OPTION_SUMMARY);

        options.addOption(help);
        options.addOption(config);
        options.addOption(summary);
        return options;
    }

    public static void main(String[] args) {
        System.out.println("Running.");
        CommandLineParser parser = new GnuParser(); // replace with BasicParser when Apache commons-cli is released
        CommandLine cmd;
        try {
            Options options = getCommandlineOptions();
            // parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            printHelp();
            return;
        }
        if (cmd.hasOption(OPTION_HELP)) {
            printHelp();
            return;
        }

        Path config = Paths.get(cmd.getOptionValue(OPTION_CONFIG)).toAbsolutePath();
        System.out.println("Config file: " + config);
        try {
            Path report = Paths.get(Util.getReportLocation(config)).toAbsolutePath();
            System.out.println("Report file: " + report);
            Path toolsRoot = WsiBasicProfileChecker.getDefaultWsiTestToolsRoot();
            WsiBasicProfileChecker.WsiProfile profile = WsiBasicProfileChecker.getDefaultWsiProfile();
            AnalysisInformationCollector collector = new AnalysisInformationCollector();
            WsiBasicProfileChecker.checkWsiBasicProfile(config, report, toolsRoot, profile, collector);
            if (cmd.hasOption(OPTION_SUMMARY)) {
                // TODO: Serialize collector - use JSON lib - for now just dump to console
                if (collector.getErrorCount() > 0)  {
                    System.out.println(" \"errors\":[");
                    String delim = "";
                    for (AnalysisInformation error :  collector.getErrors()) {
                        System.out.print(delim);
                        System.out.println(error.toJson());
                        delim = ", ";
                    }
                    System.out.println("]");
                }
                if (collector.getWarningCount() > 0)  {
                    if (collector.getErrorCount() > 0) {
                        System.out.println(",");
                    }
                    System.out.println(" \"warnings\":[");
                    String delim = "";
                    for (AnalysisInformation warning :  collector.getWarnings()) {
                        System.out.print(delim);
                        System.out.println(warning.toJson());
                        delim = ", ";
                    }
                    System.out.println("]");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception " + e.getMessage());
        }
    }

}
