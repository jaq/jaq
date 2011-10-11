package org.jaq;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Jorge Rodriguez Barba
 *         <p/>
 */
public class SqlExecutor extends BaseQueryTool {

    private static Logger logger = Logger.getLogger(SqlExecutor.class);

    public void readFileAndExecQueries(String _file) throws IOException {
        System.out.println();
        System.out.println("Process file queries...");
        System.out.println();
        FileReader fileReader = new FileReader(_file);
        BufferedReader reader = new BufferedReader(fileReader);
        String query = "";
        boolean composingQuery = false;

        String line, lineInLowercase;

        while ((line = reader.readLine()) != null) {

            lineInLowercase = line.toLowerCase().trim();

            if (lineInLowercase.startsWith("//") ||
                    lineInLowercase.startsWith("#") ||
                    lineInLowercase.startsWith("rem") ||
                    lineInLowercase.startsWith("--")) {

                logger.debug("Comments must be ignored...");
                query = "";
                composingQuery = false;

            } else if (lineInLowercase.startsWith("spool")) {
                logger.debug("Comments must be ignored...");
                query = "";
                composingQuery = false;

            } else if (lineInLowercase.length() == 0) {
                if (!composingQuery) {
                    System.out.print("");
                    query = "";
                    composingQuery = false;
                }
            } else if ((getPrivelegeMode() == 0) && (lineInLowercase.startsWith("create") || lineInLowercase.startsWith("drop")
                    || lineInLowercase.startsWith("alter"))) {
                logger.warn("create, drop and alter statements are forbidden!!!");
                System.out.print("");
                query = "";
                composingQuery = false;
            } else {
                if (composingQuery) {
                    if (line.endsWith(";")) {
                        //Se tiene la query
                        query += " " + line;
                        query = query.substring(0, query.length() - 1);
                        doQuery(query);

                        composingQuery = false;
                        query = "";
                    } else {
                        //Aun quedan lineas para terminar de componer la query
                        query += " " + line;
                    }
                } else {
                    //Query nueva
                    if (line.endsWith(";")) {
                        //Query de una linea
                        query = line.substring(0, line.length() - 1);
                        doQuery(query);
                        query = "";
                        composingQuery = false;
                    } else {
                        //Query de varias lineas
                        composingQuery = true;
                        query += line;
                    }
                }
            }
        }
        reader.close();

    }

    public void doQuery(String _query) {

        String lowerCaseQuery = _query.toLowerCase();
        logger.info("Doing query: " + _query);
        boolean isUpdate = lowerCaseQuery.startsWith("insert") ||
                lowerCaseQuery.startsWith("update") ||
                lowerCaseQuery.startsWith("delete");

        try {
            if (isUpdate) {
                int execUpdateRes = getStatement().executeUpdate(_query);
                logger.info("Rows updated: " + execUpdateRes);
            } else {
                ResultSet resultSet = getStatement().executeQuery(_query);
                logger.info("Printing results..");
                QueryResultPrinter resultPrinter= new QueryResultPrinter(resultSet);
                resultPrinter.printQueryResult();
            }
        } catch (SQLException e) {
            logger.error("Error executing: " + _query, e);
        }
    }

    public static void main(String[] args) {
        String file = "sql/example.sql";

        switch (args.length) {
            case 0:
                System.out.println("Using conf/jaq.properties and ./sql/example.sql query file");
                break;

            case 1:
                System.out.println("Using conf/jaq.properties and " + args[0] + " query file");
                file = args[0];
                break;

            default:
                System.out.println("You must be invoke: ");
                System.out.println("\t- Without arguments for running sql/example.sql ");
                System.out.println("\t- With one argument, the sql file with your queries");
                System.exit(0);
                break;
        }

        System.out.println("");
        SqlExecutor sqlExecutor = new SqlExecutor();
        try {
            sqlExecutor.loadConfiguration();
            sqlExecutor.connect();
            sqlExecutor.readFileAndExecQueries(file);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            logger.info("Trying to finalize connection...");
            try {
                sqlExecutor.disconnect();
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }
}

