package org.jaq;

import java.io.*;
import java.sql.*;
import java.util.Properties;


/**
 * @author Jorge Rodriguez Barba
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class QueryTool {
    private Connection connection = null;
    private Statement statement;
    private String url;
    private String driver;
    private String user;
    private String password;
    private String file;
    private int privelegeMode = 0;

    private void loadConfiguration() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/jaq.properties"));
            this.url = prop.getProperty("url");
            this.driver = prop.getProperty("driver");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
            this.privelegeMode = new Integer((String) prop.get("enableFullAccess"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public QueryTool(String _file) {
        this.file = _file;
    }

    public void initDatabaseConnection() throws ClassNotFoundException, SQLException {
        System.out.println("Loading oracle driver...");
        Class.forName(this.driver);

        System.out.println("Trying to connecto to " + this.url + " with credentials " + this.user + "/" + this.password);
        connection = DriverManager.getConnection(url, user, password);

        System.out.println("Creating statement...");
        this.statement = connection.createStatement();
    }

    public void finishDatabaseConnection() throws SQLException {
        System.out.println("Close statement..");
        this.statement.close();

        System.out.println("Close connection...");
        this.connection.close();
    }

    public void readExternalFileAndExecQueries(String _file) {
        System.out.println();
        System.out.println("Process file queries...");
        System.out.println();
        try {
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

                    System.out.println("Comments must be ignored...");
                    query = "";
                    composingQuery = false;

                } else if (lineInLowercase.startsWith("spool")) {

                    System.out.println("Comments must be ignored...");
                    query = "";
                    composingQuery = false;

                } else if (lineInLowercase.length() == 0) {
                    if (!composingQuery) {
                        System.out.print("");
                        query = "";
                        composingQuery = false;
                    }
                } else if ((privelegeMode == 0) && (lineInLowercase.startsWith("create") || lineInLowercase.startsWith("drop") || lineInLowercase.startsWith("alter"))) {
                    System.out.println("create, drop and alter statements are forbidden!!!");
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
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found: " + e);
            e.printStackTrace();
        } catch (java.io.IOException e1) {
            e1.printStackTrace();
        }
    }

    public void doQuery(String _query) {
        String query_in_lowercase = _query.toLowerCase();
        try {
            System.out.println("Doing query: " + _query);

            if (query_in_lowercase.startsWith("insert") || query_in_lowercase.startsWith("update") || query_in_lowercase.startsWith("delete")) {
                int execUpdateRes = statement.executeUpdate(_query);
                System.out.println("Rows updated: " + execUpdateRes);
            } else {
                ResultSet rset = statement.executeQuery(_query);
                System.out.println("Printing results..");
                printQueryResult(rset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("EXCEPCION ejecutando: " + _query);
        }
    }

    static void printQueryResult(ResultSet rset) {
        ResultSetMetaData rsetmd;
        int nCols;
        try {
            rsetmd = rset.getMetaData();
            nCols = rsetmd.getColumnCount();
            for (int i = 1; i <= nCols; i++) {
                System.out.print(rsetmd.getColumnName(i));
                int colSize = rsetmd.getColumnDisplaySize(i);
                for (int k = 0; k < colSize - rsetmd.getColumnName(i).length(); k++)
                    System.out.print(" ");
            }

            System.out.println("");

            while (rset.next()) {
                for (int i = 1; i <= nCols; i++) {
                    String val = rset.getString(i);
                    if (rset.wasNull())
                        System.out.print("null");
                    else
                        System.out.print(rset.getString(i));

                    int colSize;
                    if (rset.wasNull())
                        colSize = 4;
                    else
                        colSize = rsetmd.getColumnDisplaySize(i);

                    if (rset.wasNull()) {
                        for (int k = 0; k < colSize - 4; k++)
                            System.out.print(" ");
                    } else {
                        for (int k = 0; k < colSize - rset.getString(i).length(); k++)
                            System.out.print(" ");
                    }
                }
                System.out.println("");
            }
        } catch (SQLException e) {
            System.out.println("--- It's impossible to print query results");
        }
    }

    public void readAndProcessFile() {
        boolean checkDB = false;
        try {
            initDatabaseConnection();
            checkDB = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (checkDB) {
            readExternalFileAndExecQueries(file);
            try {
                finishDatabaseConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String file;
        QueryTool queryTool;

        switch (args.length) {
            case 0:
                System.out.println("Using Connection.properties and ./sql/example.sql");
                System.out.println("");
                file = "sql/example.sql";

                queryTool = new QueryTool(file);
                queryTool.loadConfiguration();
                queryTool.readAndProcessFile();
                break;

            case 1:
                System.out.println("Using Connection.properties and " + args[0]);
                System.out.println("");
                file = args[0];

                queryTool = new QueryTool(file);
                queryTool.loadConfiguration();
                queryTool.readAndProcessFile();
                break;

            default:
                System.out.println("You must be invoke: ");
                System.out.println("- Without arguments for running sql/example.sql ");
                System.out.println("- With one argument, the sql file with your queries");
                break;
        }
    }
}

