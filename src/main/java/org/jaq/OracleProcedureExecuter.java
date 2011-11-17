package org.jaq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * @author Jorge Rodriguez Barba
 */
public class OracleProcedureExecuter extends BaseQueryTool{

    private String fileWithPLCode;

    public OracleProcedureExecuter(String _fileWithPLCode) {
        fileWithPLCode = _fileWithPLCode;
    }

    public void enable_dbms_output(int buffer_size) {
        System.out.print("Enabling DBMS_OUTPUT - ");
        try {
            CallableStatement stmt = getConnection().prepareCall("{call sys.dbms_output.enable(?) }");
            stmt.setInt(1, buffer_size);
            stmt.execute();
            System.out.println("Enabled!");
        } catch (Exception e) {
            System.out.println("Problem occurred while trying to enable dbms_output! " + e.toString());
        }
    }

    public void printDbmsOutput() {
        System.out.println("Dumping DBMS_OUTPUT to System.out : ");
        try {
            CallableStatement stmt = getConnection().prepareCall("{call sys.dbms_output.get_line(?,?)}");
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
            stmt = getConnection().prepareCall(plcode);

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

    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        int buffer_size = 10240;
        OracleProcedureExecuter myJdbcPLSQL= new OracleProcedureExecuter("");
        myJdbcPLSQL.enable_dbms_output(buffer_size);
        myJdbcPLSQL.callPlsqlProcFromFile();
        myJdbcPLSQL.printDbmsOutput();

    }

}
