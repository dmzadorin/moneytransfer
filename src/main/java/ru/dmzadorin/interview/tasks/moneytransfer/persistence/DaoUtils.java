package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.DaoException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class DaoUtils {
    private static final Logger logger = LogManager.getLogger();

    public static <T> T executeQueryWithTransaction(DataSource datasource, String query, QueryProcessor<T> queryProcessor)
            throws DaoException {
        try (Connection conn = datasource.getConnection()) {
            conn.setAutoCommit(false);
            T result;
            try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                result = queryProcessor.doInPreparedStatement(statement);
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
