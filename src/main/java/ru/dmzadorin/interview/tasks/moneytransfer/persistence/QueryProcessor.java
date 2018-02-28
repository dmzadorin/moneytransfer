package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 * This interface acts like a Function PreparedStatement -> R, but allows to throw checked SQLException
 */
@FunctionalInterface
public interface QueryProcessor<R> {
    /**
     * Execute action with prepaeed statement and return result
     *
     * @param statement statement to do action with
     * @return some result of processing prepared statement
     * @throws SQLException  if a database access error occurs
     */
    R doInPreparedStatement(PreparedStatement statement) throws SQLException;
}
