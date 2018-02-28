package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.EntityNotFoundException;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class H2AccountDao implements AccountDao {
    private static final Logger logger = LogManager.getLogger();
    private static final String ACCOUNT_NOT_FOUND_MSG = "Account with id %d not found in storage";
    private static final String INSERT_ACCOUNT =
            "insert into ACCOUNT (FULLNAME, AMOUNT, CURRENCY, CREATE_TIME) values (?,?,?, CURRENT_TIMESTAMP())";
    private static final String GET_ACCOUNT_BY_ID = "select * from ACCOUNT where ID = ?";
    private final DataSource datasource;

    @Inject
    public H2AccountDao(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public Account createAccount(String fullName, BigDecimal initialBalance, Currency currency) {
        return DaoUtils.executeQueryWithTransaction(datasource, connection -> {
            Account account;
            logger.debug("Saving new account. Fullname: {}, amount: {}, currency: {}", fullName, initialBalance, currency);
            try (PreparedStatement statement = connection.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, fullName);
                statement.setBigDecimal(2, initialBalance);
                statement.setString(3, currency.getAlias());
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        long accountId = rs.getLong(1);
                        try (PreparedStatement stmt1 = connection.prepareStatement(GET_ACCOUNT_BY_ID)) {
                            stmt1.setLong(1, accountId);
                            try (ResultSet rs1 = stmt1.executeQuery()) {
                                if (rs1.next()) {
                                    account = getAccount(rs1);
                                } else {
                                    logger.error("Failed to save new account, generated id is not found in database");
                                    throw new SQLException(formatMsg(accountId));
                                }
                            }
                        }
                    } else {
                        logger.error("Failed to save new account, no keys generated in result set");
                        throw new SQLException("No keys generated!");
                    }
                }
            }
            return account;
        });
    }

    @Override
    public Account getAccountById(long accountId) throws EntityNotFoundException {
        return DaoUtils.executeQuery(datasource, GET_ACCOUNT_BY_ID, statement -> {
            statement.setLong(1, accountId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return getAccount(rs);
                } else {
                    throw new EntityNotFoundException(formatMsg(accountId));
                }
            }
        });
    }

    private Account getAccount(ResultSet rs) throws SQLException {
        long accountId = rs.getLong("ID");
        Currency currency = Currency.from(rs.getString("CURRENCY"));
        Timestamp timestamp = rs.getTimestamp("CREATE_TIME");
        LocalDateTime createTime = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
        BigDecimal amount = rs.getBigDecimal("AMOUNT");
        String fullName = rs.getString("FULLNAME");
        return new Account(accountId, fullName, amount, currency, createTime);
    }

    private String formatMsg(long accountId) {
        return String.format(ACCOUNT_NOT_FOUND_MSG, accountId);
    }
}
