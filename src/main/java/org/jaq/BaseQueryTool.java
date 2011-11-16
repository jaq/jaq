package org.jaq;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BaseQueryTool {

    public static final String DEFAULT_CONFIGURATION_PATH = "conf/jaq.properties";

    private Logger m_logger = Logger.getLogger(BaseQueryTool.class);

    private Connection m_connection = null;
    private Statement m_statement;
    private String m_url;
    private String m_driver;
    private String m_user;
    private String m_password;
    private int m_privelegeMode = 0;


    public BaseQueryTool() {
    }

    public Statement getStatement() {
        return m_statement;
    }

    protected void loadConfiguration(String _configPath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(_configPath));
        m_url = properties.getProperty("url");
        m_driver = properties.getProperty("driver");
        m_user = properties.getProperty("user");
        m_password = properties.getProperty("password");
        m_privelegeMode = new Integer(properties.getProperty("enableFullAccess"));
    }

    protected void connect() throws ClassNotFoundException, SQLException {
        m_logger.info("Loading driver...");
        Class.forName(m_driver);

        m_logger.info("Trying to connect to " + m_url + " with credentials " + m_user + "/" + m_password);
        m_connection = DriverManager.getConnection(m_url, m_user, m_password);

        m_logger.info("Creating m_statement...");
        m_statement = m_connection.createStatement();
        m_logger.info("\t... m_statement created");
    }

    protected void disconnect() throws SQLException {
        m_logger.info("Closing m_statement..");
        m_statement.close();
        m_logger.info("\t... m_statement closed");
        m_logger.info("Closing m_connection...");
        m_connection.close();
        m_logger.info("\t... m_connection closed");
    }

    public int getPrivelegeMode() {
        return m_privelegeMode;
    }
}
