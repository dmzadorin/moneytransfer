package ru.dmzadorin.interview.tasks.moneytransfer.config;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class DataSourceConfigurer {
    public static DataSource initDataSource(String url, String user, String pass, String initScriptPath) {
        try {
            RunScript.execute(url, user, pass, initScriptPath, StandardCharsets.UTF_8, false);
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(pass);
            return ds;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
