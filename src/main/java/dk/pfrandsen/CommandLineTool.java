package dk.pfrandsen;

import org.apache.commons.cli.*;

public abstract class CommandLineTool {

    String errorMessage = "";

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

    protected static String arg(String argument) {
        return "-" + argument;
    }

    protected abstract String getUsageString();
    protected abstract String getToolDescription();
    protected abstract Options getCommandlineOptions();
    protected abstract boolean run(CommandLine cmd);

}
