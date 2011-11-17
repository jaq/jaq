package org.jaq;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * @author Jorge Rodriguez Barba
 *         <p/>
 */
public class SqlExecutor extends BaseQueryTool {

    private Logger m_logger = Logger.getLogger(SqlExecutor.class);

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

                m_logger.debug("Comments must be ignored...");
                query = "";
                composingQuery = false;

            } else if (lineInLowercase.startsWith("spool")) {
                m_logger.debug("Comments must be ignored...");
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
                m_logger.warn("create, drop and alter statements are forbidden!!!");
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
}

