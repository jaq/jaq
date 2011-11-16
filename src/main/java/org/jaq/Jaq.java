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

    private static Logger sm_logger = Logger.getLogger(Jaq.class);

    public Options getOptions() {
        return m_options;
    }

    private Options m_options;

    public Jaq() {
        m_options = new Options();
        m_options.addOption("s", "sql", true, "Execute SQL sentence");
        m_options.addOption("c", "config", true, "Execute SQL sentence");
        m_options.addOption("p", "ping", false, "Ping Data Base");
        m_options.addOption("h", "help", false, "Print help");

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
        sm_logger.debug(builder.toString());
    }

    public static void main(String[] _args) {


        String applicationName = "Jaq";
        System.out.println("[Jaq - Just Another Query tool]");
        System.out.println();


        Jaq jaq= new Jaq();
        Options options= jaq.getOptions();

        int maxPrintColumns = 80;
        if (_args.length < 1) {

            HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(new PrintWriter(System.out), maxPrintColumns, applicationName, options);
            jaq.printHelp(options, maxPrintColumns, "Jaq help", "Enjoy using Jaq", 5, 3, true);
        }

        jaq.displayProvidedCommandLineArguments(_args);

        CommandLineParser cmdLineGnuParser = new GnuParser();
        CommandLine commandLine;
        try {
            commandLine = cmdLineGnuParser.parse(options, _args);
            if (commandLine.hasOption('p') || commandLine.hasOption("ping")) {
                sm_logger.debug("You want to ping Data Base!");
            }
            if (commandLine.hasOption('s') || commandLine.hasOption("sql")) {
                sm_logger.debug("You want to execute sql on Data Base!");
            }
            if (commandLine.hasOption('h') || commandLine.hasOption("help")) {
                jaq.printHelp(options, maxPrintColumns, "GNU HELP", "End of GNU Help", 5, 3, true);
            }
        } catch (ParseException parseException) {
            sm_logger.error("Encountered exception while parsing command line:", parseException);
        }
    }
}

