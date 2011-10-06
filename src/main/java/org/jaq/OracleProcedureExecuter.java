package org.jaq;

import java.io.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Jorge Rodriguez Barba
 */
public class OracleProcedureExecuter {

    private Connection connection;
    private String fileWithPLCode;

    private String m_host;
    private String m_port;
    private String m_sid;
    private String m_user;
    private String m_pass;
    private int m_privelegeMode;

    public OracleProcedureExecuter(String _fileWithPLCode) {
        fileWithPLCode = _fileWithPLCode;
    }

    public static void enable_dbms_output(Connection conn, int buffer_size) {
        System.out.print("Enabling DBMS_OUTPUT - ");
        try {
            CallableStatement stmt = conn.prepareCall("{call sys.dbms_output.enable(?) }");
            stmt.setInt(1, buffer_size);
            stmt.execute();
            System.out.println("Enabled!");
        } catch (Exception e) {
            System.out.println("Problem occurred while trying to enable dbms_output! " + e.toString());
        }
    }

    public static void printDbmsOutput(Connection conn) {
        System.out.println("Dumping DBMS_OUTPUT to System.out : ");
        try {
            CallableStatement stmt = conn.prepareCall("{call sys.dbms_output.get_line(?,?)}");
            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            stmt.registerOutParameter(2, java.sql.Types.NUMERIC);
            int status = 0;
            do {
                stmt.execute();
                System.out.println(" " + stmt.getString(1));
                status = stmt.getInt(2);
            } while (status == 0);
            System.out.println("End of dbms_output!");
        } catch (Exception e) {
            System.out.println("Problem occurred during dump of dbms_output! " + e.toString());
        }
    }

    private void loadConfiguration() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/jaq.properties"));
            m_host = (String) prop.get("hostName");
            m_port = (String) prop.get("port");
            m_sid = (String) prop.get("sid");
            m_user = (String) prop.get("user");
            m_pass = (String) prop.get("password");
            m_privelegeMode = new Integer((String) prop.get("enableFullAccess")).intValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initDatabaseConnection() throws ClassNotFoundException, SQLException {
        System.out.println("Loading oracle driver...");
        Class.forName("oracle.jdbc.driver.OracleDriver");

        System.out.println("Trying to connecto with jdbc:oracle:thin:" + m_user + "/" + m_pass + "@" + m_host + ":" + m_port + ":" + m_sid);
        connection = DriverManager.getConnection("jdbc:oracle:thin:" + m_user + "/" + m_pass + "@" + m_host + ":" + m_port + ":" + m_sid);

        System.out.println("Creating statement...");
    }

    private void callPlsqlProcFromFile() {
        CallableStatement stmt = null;
        String plcode = "";
        String linea = "";

        try {
            BufferedReader bf = new BufferedReader(new FileReader(fileWithPLCode));
            while ((linea = bf.readLine()) != null) {
                plcode += linea + "\n";
            }

            System.out.println("Before prepareCall...");
            stmt = connection.prepareCall(plcode);

            long fecha_ini = System.currentTimeMillis();
            System.out.println("Before execute..." + fecha_ini);
            stmt.execute();
            long fecha_fin = System.currentTimeMillis();
            System.out.println("Done " + fecha_fin);

        } catch (SQLException ex) { // Trap SQL Errors
            System.out.println("Error while Calling PL/SQL Procedure\n" + ex.toString());
        } catch (FileNotFoundException e) {
            System.out.println("pl.sql not found!");
            e.printStackTrace();
            exitApplication();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ex) {
                System.out.println("Exception on statement close!");
            }
        }
    }

    private void exitApplication() {
        try {
            if (connection != null)
                connection.close();

            System.exit(0);

        } catch (SQLException ex) { // Trap SQL Errors
            System.out.println("Error While Closing Connection .." + ex.toString());
            System.exit(0);
        }
    }

    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        int buffer_size = 10240;
        OracleProcedureExecuter myJdbcPLSQL;

        if (args.length != 1) {
            System.out.println("Using ./sql/pls/pl_example.sql like file with plsql code");
            System.out.println("");
            myJdbcPLSQL = new OracleProcedureExecuter("sql/pls/pl_example.sql");
        } else {
            System.out.println("Using " + args[0] + " like file with plsql code");
            System.out.println("");
            myJdbcPLSQL = new OracleProcedureExecuter(args[0]);
        }

        myJdbcPLSQL.initDatabaseConnection(); // Set up the db Connection

        if (myJdbcPLSQL.connection != null) {
            OracleProcedureExecuter.enable_dbms_output(myJdbcPLSQL.connection, buffer_size);
            myJdbcPLSQL.callPlsqlProcFromFile();
            OracleProcedureExecuter.printDbmsOutput(myJdbcPLSQL.connection);
            myJdbcPLSQL.exitApplication();
        }
    }
}
