package dk.pfrandsen;

import com.fasterxml.jackson.jr.ob.JSON;
import dk.pfrandsen.check.AnalysisInformationCollector;
import dk.pfrandsen.wsdl.*;
import dk.pfrandsen.wsdl.wsi.WsiBasicProfileChecker;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: Add options for specifying WS-I tools root and location of report stylesheet file

public class AnalyzeWsdl {
    static private String OPTION_CONFIG = "config";
    static private String OPTION_SUMMARY = "summary";
    static private String USAGE = "Usage: java -jar <jar-file> -" + Runner.OPTION_ANALYZE + " -" + OPTION_CONFIG
            + " <file> " + " [-" + OPTION_SUMMARY + " <file>]";

    public static void printHelp() {
        System.out.println();
        System.out.println("Tool for analyzing WSDL for WS-I compliance.");
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, getCommandlineOptions());
    }

    private static Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(Runner.OPTION_HELP, USAGE);

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
        options.addOption(new Option(Runner.OPTION_ANALYZE, "option to select this tool"));
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
        if (cmd.hasOption(Runner.OPTION_HELP)) {
            printHelp();
            return;
        }

        boolean success = true;
        Path config = Paths.get(cmd.getOptionValue(OPTION_CONFIG)).toAbsolutePath();
        System.out.println("Config file: " + config);
        try {
            Path report = Paths.get(Util.getReportLocation(config)).toAbsolutePath();
            System.out.println("Report file: " + report);
            Path toolsRoot = WsiBasicProfileChecker.getDefaultWsiTestToolsRoot();
            WsiBasicProfileChecker.WsiProfile profile = WsiBasicProfileChecker.getDefaultWsiProfile();
            AnalysisInformationCollector collector = new AnalysisInformationCollector();
            System.out.println("Analyzing wsdl...");
            WsiBasicProfileChecker.checkWsiBasicProfile(config, report, toolsRoot, profile, collector);
            if (cmd.hasOption(OPTION_SUMMARY)) {
                System.out.println("Generating summary...");
                Path summary = Paths.get(cmd.getOptionValue(OPTION_SUMMARY)).toAbsolutePath();
                JSON.std.with(JSON.Feature.PRETTY_PRINT_OUTPUT).write(collector, summary.toFile());
            }
        } catch (Exception e) {
            System.err.println("Exception " + e.getMessage());
            success = false;
        }
        System.out.println("WSDL analysis completed with status: " + (success ? "SUCCESS" : "FAILURE"));
    }

}
