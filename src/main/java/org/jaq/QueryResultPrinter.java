package org.jaq;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class QueryResultPrinter {
    private Logger logger= Logger.getLogger(QueryResultPrinter.class);
    private ResultSet resultSet;

    public QueryResultPrinter(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void printQueryResult() {
        ResultSetMetaData resultSetMetaData;
        int nCols;
        try {
            resultSetMetaData = this.resultSet.getMetaData();
            nCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= nCols; i++) {
                System.out.print(resultSetMetaData.getColumnName(i));
                int colSize = resultSetMetaData.getColumnDisplaySize(i);
                for (int k = 0; k < colSize - resultSetMetaData.getColumnName(i).length(); k++)
                    System.out.print(" ");
            }

            System.out.println("");

            while (this.resultSet.next()) {
                for (int i = 1; i <= nCols; i++) {
                    String val = this.resultSet.getString(i);
                    if (this.resultSet.wasNull())
                        System.out.print("null");
                    else
                        System.out.print(this.resultSet.getString(i));

                    int colSize;
                    if (this.resultSet.wasNull())
                        colSize = 4;
                    else
                        colSize = resultSetMetaData.getColumnDisplaySize(i);

                    if (this.resultSet.wasNull()) {
                        for (int k = 0; k < colSize - 4; k++)
                            System.out.print(" ");
                    } else {
                        for (int k = 0; k < colSize - this.resultSet.getString(i).length(); k++)
                            System.out.print(" ");
                    }
                }
                System.out.println("");
            }
        } catch (SQLException e) {
            logger.info("--- It's impossible to print query results", e);
        }
    }

}
