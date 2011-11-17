package org.jaq;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jorge Rodriguez Barba
 *         <p/>
 */
public class DataBaseViewer extends BaseQueryTool{

    private List<String> configureAllowedQueries() {
        ArrayList<String> listQueries = new ArrayList<String>();
        String query01 = "select * from t_og_user";
        listQueries.add(query01);
        String query02 = "select * from t_og_application";
        listQueries.add(query02);
        String query03 = "select * from t_og_device";
        listQueries.add(query03);
        String query04 = "select * from t_og_client";
        listQueries.add(query04);
        String query05 = "select * from t_og_brand";
        listQueries.add(query05);
        String query06 = "select * from t_og_portal";
        listQueries.add(query06);
        String query07 = "select * from t_og_deferred_sequencer_msg";
        listQueries.add(query07);
        String query08 = "select * from t_og_sidapn";
        listQueries.add(query08);
        String query09 = "select * from t_og_sidggsn";
        listQueries.add(query09);
        String query10 = "select * from t_og_sidcurrent";
        listQueries.add(query10);
        String query11 = "select * from t_og_spicurrent";
        listQueries.add(query11);
        String query12 = "select * from t_og_scpacurrent";
        listQueries.add(query12);
        String query13 = "select * from t_og_logical_delete_error";
        listQueries.add(query13);
        String query14 = "select * from all_jobs";
        listQueries.add(query14);
        String query15 = "select * from all_procedures";
        listQueries.add(query15);
        return listQueries;
    }

    private void sqlplus() {
        Connection conn = null;
        String query;
        List<String> listQueries = configureAllowedQueries();

        try {
            //loadConfiguration();

            connect();

            System.out.println("Creating statement...");
            Statement stmt = conn.createStatement();

            String option;
            do {
                printMenu();
                option = readEntry("Input option: ");
                System.out.println("");

                if (option.equalsIgnoreCase("quit")) {
                    break;
                }

                if (option.equalsIgnoreCase("queries")) {
                    printListQueries(listQueries);
                } else {

                    try {
                        int optionNumber = Integer.parseInt(option);
                        query = listQueries.get(optionNumber);
                        System.out.println("*****" + option + " ***** " + query);

                        System.out.println("Doing query: " + query);
                        ResultSet rset = stmt.executeQuery(query);

                        System.out.println("Printing results..");
                        printQueryResult(rset);

                    } catch (NumberFormatException e) {
                        // TODO: handle exception
                        System.out.println("Query number not exists!");
                    } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                        System.out.println("Query number not exists!");
                    }
                }

            } while (true);

            System.out.println("Close statement..");
            stmt.close();

            System.out.println("Close connection...");
            conn.close();

        } catch (SQLException e1) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            e1.printStackTrace();
            throw new RuntimeException("Connection error!!!", e1);
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load the driver");
            throw new RuntimeException("Driver not found!!!", e);
        }
    }

    private void printQueryResult(ResultSet resultSet) {
        ResultSetMetaData resultSetMetaData;
        int nCols;
        try {
            resultSetMetaData = resultSet.getMetaData();
            nCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= nCols; i++) {
                System.out.print(resultSetMetaData.getColumnName(i));
                int colSize = resultSetMetaData.getColumnDisplaySize(i);
                for (int k = 0; k < colSize - resultSetMetaData.getColumnName(i).length(); k++)
                    System.out.print(" ");
            }

            System.out.println("");

            while (resultSet.next()) {
                for (int i = 1; i <= nCols; i++) {
                    String val = resultSet.getString(i);
                    if (resultSet.wasNull())
                        System.out.print("null");
                    else
                        System.out.print(resultSet.getString(i));

                    int colSize;
                    if (resultSet.wasNull())
                        colSize = 4;
                    else
                        colSize = resultSetMetaData.getColumnDisplaySize(i);

                    if (resultSet.wasNull()) {
                        for (int k = 0; k < colSize - 4; k++)
                            System.out.print(" ");
                    } else {
                        for (int k = 0; k < colSize - resultSet.getString(i).length(); k++)
                            System.out.print(" ");
                    }
                }
                System.out.println("");
            }

        } catch (SQLException e) {
            System.out.println("--- It's impossible to print query results");
        }
    }

    private void printMenu() {
        System.out.println("===========================");
        System.out.println("PSC database viewer");
        System.out.println(" Options:");
        System.out.println("  queries -> to view querys");
        System.out.println("  queryNumber -> to invoke query name");
        System.out.println("  quit -> to exit");
        System.out.println("");
    }

    private void printListQueries(List<String> _listQuerys) {
        Iterator<String> stringIterator = _listQuerys.iterator();

        int i = 0;
        System.out.println("queryNumber - query");
        while (stringIterator.hasNext()) {
            String element = stringIterator.next();
            System.out.print(i);
            System.out.print(" - ");
            System.out.println(element);
            i++;
        }
    }

    private String readEntry(String prompt) {
        try {
            StringBuilder buffer = new StringBuilder();
            System.out.print(prompt);
            System.out.flush();
            int c = System.in.read();
            while (c != '\n' && c != -1) {
                buffer.append((char) c);
                c = System.in.read();
            }
            return buffer.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }
}
