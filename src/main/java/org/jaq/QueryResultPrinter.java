package org.jaq;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class QueryResultPrinter {
    private Logger m_logger = Logger.getLogger(QueryResultPrinter.class);
    private ResultSet m_resultSet;

    public QueryResultPrinter(ResultSet resultSet) {
        this.m_resultSet = resultSet;
    }

    public void printQueryResult() {
        ResultSetMetaData resultSetMetaData;
        int columnCount;
        try {
            resultSetMetaData = this.m_resultSet.getMetaData();
            columnCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSetMetaData.getColumnName(i));
                int colSize = resultSetMetaData.getColumnDisplaySize(i);
                for (int k = 0; k < colSize - resultSetMetaData.getColumnName(i).length(); k++)
                    System.out.print(" ");
            }

            System.out.println("");

            while (this.m_resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String val = this.m_resultSet.getString(i);

                    if (this.m_resultSet.wasNull())
                        System.out.print("null");
                    else
                        System.out.print(this.m_resultSet.getString(i));

                    int colSize;

                    if (this.m_resultSet.wasNull())
                        colSize = 4;
                    else
                        colSize = resultSetMetaData.getColumnDisplaySize(i);

                    if (this.m_resultSet.wasNull()) {
                        for (int k = 0; k < colSize - 4; k++)
                            System.out.print(" ");
                    } else {
                        for (int k = 0; k < colSize - this.m_resultSet.getString(i).length(); k++)
                            System.out.print(" ");
                    }
                }
                System.out.println("");
            }
        } catch (SQLException e) {
            m_logger.info("--- It's impossible to print query results", e);
        }
    }

}
