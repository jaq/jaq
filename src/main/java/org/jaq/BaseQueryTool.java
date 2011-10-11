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

    private Connection connection = null;
    private Logger logger= Logger.getLogger(BaseQueryTool.class);

    public BaseQueryTool() {
        BasicConfigurator.configure();
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    public Statement getStatement() {
        return statement;
    }

    private Statement statement;
    private String url;
    private String driver;
    private String user;
    private String password;
    private int privelegeMode = 0;

    protected void loadConfiguration() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("conf/jaq.properties"));
        this.url = prop.getProperty("url");
        this.driver = prop.getProperty("driver");
        this.user = prop.getProperty("user");
        this.password = prop.getProperty("password");
        this.privelegeMode = new Integer(prop.getProperty("enableFullAccess"));
    }

    protected void initDatabaseConnection() throws ClassNotFoundException, SQLException {
        logger.info("Loading oracle driver...");
        Class.forName(this.driver);

        logger.info("Trying to connect to " + this.url + " with credentials " + this.user + "/" + this.password);
        connection = DriverManager.getConnection(url, user, password);

        logger.info("Creating statement...");
        this.statement = connection.createStatement();
        logger.info("\t... statement created");
    }

    protected void finishDatabaseConnection() throws SQLException {
        logger.info("Closing statement..");
        this.statement.close();
        logger.info("\t... statement closed");
        logger.info("Closing connection...");
        this.connection.close();
        logger.info("\t... connection closed");
    }

    public int getPrivelegeMode() {
        return privelegeMode;
    }
}
