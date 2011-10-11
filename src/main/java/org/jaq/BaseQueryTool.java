package org.jaq;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BaseQueryTool {

    private Connection m_connection = null;
    private Logger     logger     = Logger.getLogger(BaseQueryTool.class);
    private Statement statement;
    private String    url;
    private String    driver;
    private String    user;
    private String    password;
    private int privelegeMode = 0;


    public BaseQueryTool() {
    }

    public Statement getStatement() {
        return statement;
    }

    protected void loadConfiguration() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("conf/jaq.properties"));
        this.url = properties.getProperty("url");
        this.driver = properties.getProperty("driver");
        this.user = properties.getProperty("user");
        this.password = properties.getProperty("password");
        this.privelegeMode = new Integer(properties.getProperty("enableFullAccess"));
    }

    protected void connect() throws ClassNotFoundException, SQLException {
        logger.info("Loading oracle driver...");
        Class.forName(this.driver);

        logger.info("Trying to connect to " + this.url + " with credentials " + this.user + "/" + this.password);
        m_connection = DriverManager.getConnection(url, user, password);

        logger.info("Creating statement...");
        this.statement = m_connection.createStatement();
        logger.info("\t... statement created");
    }

    protected void disconnect() throws SQLException {
        logger.info("Closing statement..");
        this.statement.close();
        logger.info("\t... statement closed");
        logger.info("Closing m_connection...");
        m_connection.close();
        logger.info("\t... m_connection closed");
    }

    public int getPrivelegeMode() {
        return privelegeMode;
    }
}
