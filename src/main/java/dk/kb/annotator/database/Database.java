package dk.kb.annotator.database;

import dk.kb.annotator.config.ServiceConfig;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    public Database() {}

    public static Connection getConnection() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
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
