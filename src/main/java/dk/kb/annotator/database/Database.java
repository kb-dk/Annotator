package dk.kb.annotator.database;

import dk.kb.annotator.config.ServiceConfig;
import org.apache.log4j.Logger;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {

    private static final Logger logger = Logger.getLogger(Database.class);

    public Database() {}

    public static Connection getConnection() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Properties props = Driver.parseURL("",null);
        dataSource.setServerNames(new String[]{ServiceConfig.getDatabaseHost()});
        dataSource.setPortNumbers(new int[]{ServiceConfig.getDatabasePort()});
        dataSource.setUser(ServiceConfig.getDatabaseUser());
        dataSource.setPassword(ServiceConfig.getDatabasePassword());
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
