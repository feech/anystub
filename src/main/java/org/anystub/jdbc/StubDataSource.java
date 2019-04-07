package org.anystub.jdbc;

import org.anystub.Base;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class StubDataSource implements DataSource {

    final private DataSource realDataSource;
    final private Base base;

    public StubDataSource(DataSource realDataSource, Base base) {
        this.realDataSource = realDataSource;
        this.base = base;
    }

    public StubDataSource(DataSource realDataSource, Base base, Spier spier) {
        this.realDataSource = realDataSource;
        this.base = base;
    }

    @Override
    public Connection getConnection() throws SQLException {
        StubConnection stubConnection = new StubConnection( this);
        return spy(stubConnection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        StubConnection stubConnection = new StubConnection(username, password, this);
        return spy(stubConnection);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }


    private Connection spy(Connection connection) {
        return SpierProvider.getSpier() == null ? connection : SpierProvider.getSpier().spy(connection);
    }


    public Base getBase() {
        return base;
    }

    public DataSource getRealDataSource() {
        return realDataSource;
    }

}