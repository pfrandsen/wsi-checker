package dk.pfrandsen;

import org.apache.commons.cli.*;

public abstract class CommandLineTool {

    String errorMessage = "";

    protected static String arg(String argument) {
        return "-" + argument;
    }

    public void printHelp() {
        System.out.println();
        System.out.println(getToolDescription());
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getUsageString(), getCommandlineOptions());
    }

    protected CommandLine parseCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser(); // replace with BasicParser when Apache commons-cli is released
        return parser.parse(getCommandlineOptions(), args);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public abstract String getUsageString();

    public abstract String getToolDescription();

    public abstract Options getCommandlineOptions();

    public abstract boolean run(CommandLine cmd);

    public abstract String getStatusMessage(boolean runStatus);

}
