package org.jaq;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class BaseQueryTool {

    private Logger m_logger = Logger.getLogger(BaseQueryTool.class);

    private Connection m_connection = null;
    //private Statement m_statement;
    private String m_url;
    private String m_driver;
    private String m_user;
    private String m_password;
    private int m_privelegeMode = 0;
    private String m_configFilePath= null;
    private Properties m_properties;

    public String getProperty(String _property){
        return m_properties.getProperty(_property);
    }

    protected void loadConfiguration(String _configPath) throws IOException {
        if (_configPath != null) {
            m_configFilePath = _configPath;
        }
        m_properties = new Properties();
        m_properties.load(new FileInputStream(m_configFilePath));
        m_url = m_properties.getProperty("url");
        m_driver = m_properties.getProperty("driver");
        m_user = m_properties.getProperty("user");
        m_password = m_properties.getProperty("password");
        m_privelegeMode = new Integer(m_properties.getProperty("enableFullAccess"));
    }


    protected void connect() throws ClassNotFoundException, SQLException {
        m_logger.info("Loading driver...");
        Class.forName(m_driver);

        m_logger.info("Trying to connect to " + m_url + " with credentials " + m_user + "/" + m_password);
        m_connection = DriverManager.getConnection(m_url, m_user, m_password);

        //m_logger.debug("Creating statement...");
        //m_statement = m_connection.createStatement();
        //m_logger.debug("... statement created");
    }

    protected void disconnect() throws SQLException {
        m_logger.debug("Closing statement..");
        //m_statement.close();
        m_logger.debug("... statement closed");
        m_logger.debug("Closing m_connection...");
        m_connection.close();
        m_logger.debug("... connection closed");
    }

    public int getPrivelegeMode() {
        return m_privelegeMode;
    }

    public void doQuery(String _query) {

        String lowerCaseQuery = _query.toLowerCase().trim();
        m_logger.info("Trying to execute query: " + _query);
        boolean isUpdate = lowerCaseQuery.startsWith("insert") ||
                lowerCaseQuery.startsWith("update") ||
                lowerCaseQuery.startsWith("delete");

        try {
            m_logger.debug("Creating statement...");
            Statement statement = m_connection.createStatement();
            m_logger.debug("... statement created");

            try {
                if (isUpdate) {
                    int execUpdateRes = statement.executeUpdate(_query.trim());
                    m_logger.info("Rows updated: " + execUpdateRes);
                } else {
                    ResultSet resultSet = statement.executeQuery(_query.trim());
                    m_logger.info("Printing results..");
                    QueryResultPrinter resultPrinter = new QueryResultPrinter(resultSet);
                    resultPrinter.printQueryResult();
                }
            } catch (SQLException e) {
                m_logger.error("Error executing: " + _query);
                m_logger.error("Error detail: "+e.getMessage());
            }
            statement.close();

        } catch (SQLException e){
            m_logger.error("Error creating statement");
        }
    }

    public void ping(){
        try {
            m_logger.debug("Creating statement...");
            Statement statement = m_connection.createStatement();
            m_logger.debug("... statement created");

            try {
                ResultSet resultSet = statement.executeQuery(getProperty("query.ping"));
                m_logger.info("Ping OK");
            } catch (SQLException e) {
                m_logger.error("Ping KO");
            }
            statement.close();

        } catch (SQLException e){
            m_logger.error("Error creating statement");
        }


    }

    public Connection getConnection() {
        return m_connection;
    }

}
