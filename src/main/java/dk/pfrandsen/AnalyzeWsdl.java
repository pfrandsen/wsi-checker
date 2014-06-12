package dk.pfrandsen;

import com.fasterxml.jackson.jr.ob.JSON;
import dk.pfrandsen.check.AnalysisInformationCollector;
import dk.pfrandsen.wsdl.*;
import dk.pfrandsen.wsdl.wsi.WsiBasicProfileChecker;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: Add options for specifying WS-I tools root and location of report stylesheet file

public class AnalyzeWsdl extends CommandLineTool {
    public static String ASSERTION_ID = "WS-I WSDL validation runner";
    private static String OPTION_ROOT = "root";
    static private String OPTION_CONFIG = "config";
    static private String OPTION_SUMMARY = "summary";
    static private String USAGE = "Usage: java -jar <jar-file> " + arg(Runner.OPTION_ANALYZE) + " "
            + arg(OPTION_ROOT) + " <file> " + arg(OPTION_CONFIG) + " <file> " + " [" + arg(OPTION_SUMMARY) + " <file>]";

    @Override
    protected String getUsageString() {
        return USAGE;
    }

    @Override
    protected String getToolDescription() {
        return "Tool for analyzing WSDL for WS-I compliance.";
    }

    protected Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(Runner.OPTION_HELP, USAGE);
        Option root = new Option(OPTION_ROOT, true, "WS-I tools root folder (containing schemas, stylesheets etc.)");
        root.setArgName("folder");
        Option config = new Option(OPTION_CONFIG, true, "location of configuration file");
        config.setArgName("file");
        Option summary = new Option(OPTION_SUMMARY, false, "location of summary file");
        summary.setArgName("file");
        options.addOption(help);
        options.addOption(new Option(Runner.OPTION_ANALYZE, "option to select this tool"));
        options.addOption(root);
        options.addOption(config);
        options.addOption(summary);
        return options;
    }

    @Override
    protected boolean run(CommandLine cmd) {
        Path rootFolder = Paths.get(cmd.getOptionValue(OPTION_ROOT)).toAbsolutePath();
        Path configFile = Paths.get(cmd.getOptionValue(OPTION_CONFIG));
        AnalysisInformationCollector collector = new AnalysisInformationCollector();
        if (cmd.hasOption(OPTION_SUMMARY)) {
            Path summaryFile = Paths.get(cmd.getOptionValue(OPTION_SUMMARY));
            return analyzeWsdl(rootFolder, configFile, summaryFile, collector);
        } else {
            return analyzeWsdl(rootFolder, configFile, collector);
        }
    }

    public boolean analyzeWsdl(Path rootFolder, Path configFile, AnalysisInformationCollector collector) {
        try {
            Path reportFile = Paths.get(Util.getReportLocationFromConfigFile((configFile))); // .toAbsolutePath();
            System.out.println("Report file: " + reportFile);
            System.out.println("Report file: " + reportFile.toAbsolutePath());
            System.out.println("Analyzing wsdl...");
            WsiBasicProfileChecker.checkWsiBasicProfile(configFile, reportFile, rootFolder, collector);
            return true;
        } catch (Exception e) {
            collector.addError(ASSERTION_ID, "Exception while running analyzer",
                    AnalysisInformationCollector.SEVERITY_LEVEL_CRITICAL, e.getMessage());
            setErrorMessage("Exception " + e.getMessage());
            return false;
        }
    }

    public boolean analyzeWsdl(Path rootFolder, Path configFile, Path summaryFile,
                               AnalysisInformationCollector collector) {
        boolean analysisStatus = analyzeWsdl(rootFolder, configFile, collector);
        try {
            JSON.std.with(JSON.Feature.PRETTY_PRINT_OUTPUT).write(collector, summaryFile.toFile());
        } catch (IOException e) {
            collector.addError(ASSERTION_ID, "Exception while writing summary file",
                    AnalysisInformationCollector.SEVERITY_LEVEL_MAJOR, e.getMessage());
            setErrorMessage("Exception " + e.getMessage());
            return false;
        }
        return analysisStatus;
    }

    public static void main(String[] args) {
        System.out.println("Running.");
        AnalyzeWsdl tool = new AnalyzeWsdl();
        CommandLine cmd;
        try {
            cmd = tool.parseCommandLine(args);
        } catch (ParseException e) {
            tool.printHelp();
            return;
        }
        boolean success = tool.run(cmd);
        System.out.println("WSDL analysis completed with status: " + (success ? "SUCCESS" : "FAILURE"));
    }

}
