package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.DaoException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 * Utility classes for working with database
 */
public class DaoUtils {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Initiates connection, creates prepared statement and processes it with supplied queryProcessor
     *
     * @param datasource          datasource that will be used by connection
     * @param connectionProcessor some action to do in connection
     * @param <T>                 type of return value
     * @return result of applying connection processor to connection
     * @throws DaoException if a database access error occurs (this exception wraps SQLException)
     */
    public static <T> T executeQueryWithTransaction(DataSource datasource, ConnectionProcessor<T> connectionProcessor)
            throws DaoException {
        try (Connection conn = datasource.getConnection()) {
            conn.setAutoCommit(false);
            T result;

            try {
                result = connectionProcessor.doInConnection(conn);
                conn.commit();
            } catch (SQLException e) {
                logger.error("Failed to execute sql action due to error", e);
                conn.rollback();
                throw new DaoException("Internal error occurred", e);
            } finally {
                conn.setAutoCommit(true);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Failed to execute sql action due to error", e);
            throw new DaoException("Internal error occurred", e);
        }
    }

    /**
     * Initiates connection, creates prepared statement and processes it with supplied queryProcessor
     *
     * @param datasource     datasource that will be used by connection
     * @param query          query to execute
     * @param queryProcessor some action to do with prepared statement
     * @param <T>            type of return value
     * @return result of processing prepared statement
     * @throws DaoException if a database access error occurs (this exception wraps SQLException)
     */
    public static <T> T executeQuery(DataSource datasource, String query, QueryProcessor<T> queryProcessor)
            throws DaoException {
        try (Connection conn = datasource.getConnection()) {
            T result;
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                result = queryProcessor.doInPreparedStatement(statement);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Failed to execute sql action due to error", e);
            throw new DaoException("Internal error occurred", e);
        }
    }
}
