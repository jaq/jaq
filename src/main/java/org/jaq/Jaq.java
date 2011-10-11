package org.jaq;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.PrintWriter;

/**
 * @author Carlos Javier Prados Hijón
 */
public class Jaq {

    private static Logger logger= Logger.getLogger(Jaq.class);

    public Options getOptions() {
        return options;
    }

    private Options options;

    public Jaq() {
        options= new Options();
        options.addOption("s", "sql", false, "Execute SQL sentence");
        options.addOption("p", "ping", false, "Ping Data Base");
        options.addOption("h", "help", false, "Print help");

        BasicConfigurator.configure();
        PropertyConfigurator.configure("conf/log4j.properties");

    }

    private void printHelp(Options options, int printedRowWidth, String header, String footer, int spacesBeforeOption, int spacesBeforeOptionDescription, boolean displayUsage) {
        String commandLineSyntax = "java -cp ApacheCommonsCLI.jar";

        PrintWriter writer = new PrintWriter(System.out);

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                commandLineSyntax,
                header,
                options,
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                footer,
                displayUsage);

        writer.flush();
    }

    public void displayProvidedCommandLineArguments(String[] commandLineArguments) {
        StringBuilder builder = new StringBuilder();
        for (String argument : commandLineArguments) {
            builder.append(argument).append(" ");
        }
        logger.debug(builder.toString());
    }

    public static void main(String[] commandLineArguments) {


        String applicationName = "JAQ";
        System.out.println("[JAQ - Just Another Query tool]");
        System.out.println();


        Jaq jaq= new Jaq();
        Options options= jaq.getOptions();

        int maxPrintColumns = 80;
        if (commandLineArguments.length < 1) {

            HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(new PrintWriter(System.out), maxPrintColumns, applicationName, options);

            jaq.printHelp(options, maxPrintColumns, "GNU HELP", "End of GNU Help", 5, 3, true);
        }

        jaq.displayProvidedCommandLineArguments(commandLineArguments);

        CommandLineParser cmdLineGnuParser = new GnuParser();
        CommandLine commandLine;
        try {
            commandLine = cmdLineGnuParser.parse(options, commandLineArguments);
            if (commandLine.hasOption('p') || commandLine.hasOption("ping")) {
                logger.debug("You want to ping Data Base!");
            }
            if (commandLine.hasOption('s') || commandLine.hasOption("sql")) {
                logger.debug("You want to execute sql on Data Base!");
            }
            if (commandLine.hasOption('h') || commandLine.hasOption("help")) {
                jaq.printHelp(options, maxPrintColumns, "GNU HELP", "End of GNU Help", 5, 3, true);
            }
        } catch (ParseException parseException) {
            logger.error("Encountered exception while parsing command line:", parseException);
        }
    }
}

