package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
@FunctionalInterface
public interface QueryProcessor<R> {
   R doInPreparedStatement(PreparedStatement statement) throws SQLException;
}
