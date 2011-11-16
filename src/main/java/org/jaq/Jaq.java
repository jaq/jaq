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
    public static final int SPACES_BEFORE_OPTION = 5;
    public static final int SPACES_BEFORE_OPTION_DESCRIPTION = 3;
    public static final int MAX_PRINT_COLUMNS = 80;
    public static final boolean DISPLAY_USAGE = true;

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

    private void printHelp(String _header, String _footer) {

        String commandLineSyntax = "java -cp ApacheCommonsCLI.jar";

        PrintWriter writer = new PrintWriter(System.out);

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                MAX_PRINT_COLUMNS,
                commandLineSyntax,
                _header,
                m_options,
                SPACES_BEFORE_OPTION,
                SPACES_BEFORE_OPTION_DESCRIPTION,
                _footer,
                DISPLAY_USAGE);

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


        Jaq jaq = new Jaq();
        Options options = jaq.getOptions();

        if (_args.length < 1) {

            HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(new PrintWriter(System.out), MAX_PRINT_COLUMNS, applicationName, options);
            jaq.printHelp("Jaq help", "Enjoy using Jaq");
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
                jaq.printHelp("GNU HELP", "End of GNU Help");
            }
        } catch (ParseException parseException) {
            sm_logger.error("Encountered exception while parsing command line:", parseException);
        }
    }
}

