package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 * This interface acts like a Function Connection -> R, but allows to throw checked SQLException
 * Such processor is useful, when it's needed to do several actions within one transaction
 */
@FunctionalInterface
public interface ConnectionProcessor<R> {
    /**
     * Execute action with connection and return result
     *
     * @param connection connection to do action with
     * @return some result of processing connection
     * @throws SQLException if a database access error occurs
     */
    R doInConnection(Connection connection) throws SQLException;
}
