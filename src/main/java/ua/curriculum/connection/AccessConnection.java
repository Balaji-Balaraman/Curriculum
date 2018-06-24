package ua.curriculum.connection;

import java.sql.Connection;

public class AccessConnection {
    private Connection connection;
    private static AccessConnection ourInstance = new AccessConnection();

    public static AccessConnection getInstance() {
        return ourInstance;
    }

    private AccessConnection() {
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
