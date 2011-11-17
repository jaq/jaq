package org.jaq;

import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.SimpleCompletor;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * @author Carlos Javier Prados Hijón
 */
public class Jaq {

    private static Logger sm_logger = Logger.getLogger(Jaq.class);
    private static final String DEFAULT_LOG_CONFIG_FILE_PATH = "conf/log4j.properties";
    public static final int SPACES_BEFORE_OPTION = 5;
    public static final int SPACES_BEFORE_OPTION_DESCRIPTION = 3;
    public static final int MAX_PRINT_COLUMNS = 80;
    public static final boolean DISPLAY_USAGE = true;
    public static final String COMMAND_LINE_SYNTAX = "jaq";
    public static final String APPLICATION_NAME = "Jaq";
    public static final String DEFAULT_JAQ_CONFIGURATION_PATH = "conf/jaq.properties";

    public Options getOptions() {
        return m_options;
    }

    private Options m_options;

    public Jaq() {
        m_options = new Options();
        m_options.addOption("s", "sql", true, "Execute SQL sentences from file");
        m_options.addOption("i", "interactive", false, "Enter interactive mode");
        m_options.addOption("c", "config", true, "Config file path");
        m_options.addOption("p", "doPing", false, "Ping Data Base");
        m_options.addOption("h", "help", false, "Print help");
        m_options.addOption("v", "verbose", false, "Activate verbose mode");
    }

    private void printHelp(String _header, String _footer) {

        PrintWriter writer = new PrintWriter(System.out);

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                MAX_PRINT_COLUMNS,
                COMMAND_LINE_SYNTAX,
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

        BasicConfigurator.configure();
        PropertyConfigurator.configure(DEFAULT_LOG_CONFIG_FILE_PATH);

        sm_logger.info("[Jaq - Just Another Query tool]");

        Jaq jaq = new Jaq();
        Options options = jaq.getOptions();

        if (_args.length < 1) {
            HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(new PrintWriter(System.out), MAX_PRINT_COLUMNS, APPLICATION_NAME, options);
            jaq.printHelp("Jaq help", "Enjoy using Jaq");
        }

        jaq.displayProvidedCommandLineArguments(_args);

        CommandLineParser cmdLineGnuParser = new GnuParser();
        boolean verbose = false;
        try {
            CommandLine commandLine = cmdLineGnuParser.parse(options, _args);

            if (commandLine.hasOption('v') || commandLine.hasOption("verbose")) {
                verbose = true;
            }
            String configPath = DEFAULT_JAQ_CONFIGURATION_PATH;
            if (commandLine.hasOption('c') || commandLine.hasOption("config")) {
                configPath = commandLine.getOptionValue('c', DEFAULT_JAQ_CONFIGURATION_PATH);
            }

            if (commandLine.hasOption('p') || commandLine.hasOption("ping")) {
                sm_logger.info("Pinging database...");
                SqlExecutor sqlExecutor = new SqlExecutor();
                try {
                    sqlExecutor.loadConfiguration(configPath);
                    sqlExecutor.connect();
                    sqlExecutor.ping();
                    sqlExecutor.disconnect();
                } catch (IOException e) {
                    sm_logger.error("Can't find configuration file " + commandLine);
                } catch (ClassNotFoundException e) {
                    sm_logger.error("Error loading driver", e);
                } catch (SQLException e) {
                    sm_logger.error("Error executing SQL", e);
                }
            }
            if (commandLine.hasOption('i') || commandLine.hasOption("interactive")) {
                sm_logger.info("Interactive mode");

                SqlExecutor sqlExecutor = new SqlExecutor();
                try {
                    sqlExecutor.loadConfiguration(configPath);
                    sqlExecutor.connect();
                } catch (IOException e) {
                    sm_logger.error("Can't find configuration file " + commandLine);
                } catch (ClassNotFoundException e) {
                    sm_logger.error("Error loading driver", e);
                } catch (SQLException e) {
                    sm_logger.error("Error connecting to data base", e);
                }

                try {
                    ConsoleReader consoleReader = new ConsoleReader();
                    consoleReader.setDefaultPrompt("jaq>");
                    consoleReader.addCompletor(new ArgumentCompletor(new SimpleCompletor(new String[]{"select",
                            "delete", "insert", "update", "exit"})));
                    String command = consoleReader.readLine();
                    while (!command.equalsIgnoreCase("exit")) {
                        sqlExecutor.doQuery(command);
                        command = consoleReader.readLine();
                    }
                } catch (IOException e) {
                    sm_logger.error("Error reading input: " + e.getMessage());
                }

                try {
                    sqlExecutor.disconnect();
                } catch (SQLException e) {
                    sm_logger.error("Error executing SQL", e);
                }
            }
            if (commandLine.hasOption('s') || commandLine.hasOption("sql")) {
                SqlExecutor sqlExecutor = new SqlExecutor();
                try {
                    sqlExecutor.loadConfiguration(configPath);
                    sqlExecutor.connect();
                    sqlExecutor.readFileAndExecQueries(commandLine.getOptionValue('s', "conf/jaq.sql"));
                    sqlExecutor.disconnect();
                } catch (IOException e) {
                    sm_logger.error("Can't find configuration file " + commandLine);
                } catch (ClassNotFoundException e) {
                    sm_logger.error("Error loading driver", e);
                } catch (SQLException e) {
                    sm_logger.error("Error executing SQL", e);
                }
            }
            if (commandLine.hasOption('h') || commandLine.hasOption("help")) {
                jaq.printHelp("JAQ help", "=================================================");
            }
        } catch (ParseException e) {
            if (verbose) {
                sm_logger.error("Error", e);
            } else {
                sm_logger.error(e.getMessage());
            }
        }
    }
}

