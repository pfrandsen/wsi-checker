package dk.pfrandsen;

import org.apache.commons.cli.*;

public class Runner {
    public static String OPTION_ROOT = "root";  // option shared by all tools
    public static String OPTION_ANALYZE = "analyze";
    public static String OPTION_CONFIG_FILE = "generateConfig";
    public static String OPTION_UNPACK = "unpackTool";
    public static String OPTION_HELP = "help";
    static private String USAGE = "to get help for tool use: java -jar <jar-file> -" + OPTION_HELP + " -<tool>";

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        options.addOption(new Option(OPTION_HELP, "Print this message"));
        options.addOption(new Option(OPTION_ANALYZE, "Get help on WSDL analyzer tool"));
        options.addOption(new Option(OPTION_CONFIG_FILE, "Get help on configuration file generator tool"));
        options.addOption(new Option(OPTION_UNPACK, "Get help on file unpack tool"));
        formatter.printHelp(USAGE, options);
    }

    private static void addDummyOption(Options options, String opt, boolean hasArg) {
        Option op = new Option(opt, hasArg, "");
        if (hasArg) {
            op.setArgName("arg");
        }
        options.addOption(op);
    }

    private static Options getCommandlineOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.create(OPTION_HELP));
        options.addOption(OptionBuilder.create(OPTION_ANALYZE));
        options.addOption(OptionBuilder.create(OPTION_CONFIG_FILE));
        options.addOption(OptionBuilder.create(OPTION_UNPACK));

        // Add options for all the "sub" tools to avoid command line parsing error
        // shared option(s)
        addDummyOption(options, OPTION_ROOT, true);
        // options for analyzer tool
        addDummyOption(options, AnalyzeWsdl.OPTION_CONFIG, true);
        addDummyOption(options, AnalyzeWsdl.OPTION_SUMMARY, true);
        // options for configuration file generator
        addDummyOption(options, GenerateConfigFile.OPTION_WSDL, true);
        addDummyOption(options, GenerateConfigFile.OPTION_REPORT, true);
        addDummyOption(options, GenerateConfigFile.OPTION_OUTPUT, true);
        addDummyOption(options, GenerateConfigFile.OPTION_BINDING, true);
        addDummyOption(options, GenerateConfigFile.OPTION_STYLESHEET, true);
        addDummyOption(options, GenerateConfigFile.OPTION_PROFILE_NAME, true);
        addDummyOption(options, GenerateConfigFile.OPTION_PROFILE_FILE, true);
        addDummyOption(options, GenerateConfigFile.OPTION_TEMPLATE, true);
        return options;
    }

    static CommandLineTool getTool(CommandLine cmd) {
        if (cmd.hasOption(OPTION_ANALYZE)) {
            return new AnalyzeWsdl();
        }
        if (cmd.hasOption(OPTION_CONFIG_FILE)) {
            return new GenerateConfigFile();
        }
        if (cmd.hasOption(OPTION_UNPACK)) {
            return new UnpackTool();
        }
        return null;
    }

    public static void main(String[] args) {
        CommandLineParser parser = new GnuParser(); // replace with BasicParser when Apache commons-cli is released
        CommandLine cmd;
        try {
            Options options = getCommandlineOptions();
            // parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            System.out.println("parse error");
            printHelp();
            return;
        }

        CommandLineTool tool = getTool(cmd);
        if (null == tool) {
            System.out.println("Required option -" + OPTION_ANALYZE + " or -" + OPTION_CONFIG_FILE + " or -"
                    + OPTION_UNPACK + " not provided");
            printHelp();
            return;
        }
        if (cmd.hasOption(OPTION_HELP)) {
            tool.printHelp();
            return;
        }

        try {
            cmd = tool.parseCommandLine(args);
        } catch (ParseException e) {
            tool.printHelp();
            return;
        }
        boolean success = tool.run(cmd);
        System.out.println(tool.getStatusMessage(success));
    }

}