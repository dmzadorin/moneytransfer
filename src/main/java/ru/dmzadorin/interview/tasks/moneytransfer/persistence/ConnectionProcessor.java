package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 28.02.2018
 */
@FunctionalInterface
public interface ConnectionProcessor<R> {
    R doInConnection(Connection connection) throws SQLException;
}
