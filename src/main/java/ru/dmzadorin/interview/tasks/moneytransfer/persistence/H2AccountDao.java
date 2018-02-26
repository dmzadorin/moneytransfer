package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.AccountNotFoundException;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class H2AccountDao implements AccountDao {
    private static final Logger logger = LogManager.getLogger();
    private static final String INSERT_ACCOUNT =
            "insert into ACCOUNT (FULLNAME, AMOUNT, CURRENCY, CREATE_TIME) values (?,?,?, CURRENT_TIMESTAMP())";
    private static final String FIND_ACCOUNT_BY_ID = "select * from ACCOUNT where ID = ?";
    private static final String GET_ACCOUNT_CREATION_TIME = "select CREATE_TIME from ACCOUNT where ID = ?";
    private final DataSource datasource;

    @Inject
    public H2AccountDao(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public Long saveAccount(String fullName, BigDecimal amount, Currency currency) {
        return DaoUtils.executeQueryWithTransaction(datasource, INSERT_ACCOUNT, statement -> {
            statement.setString(1, fullName);
            statement.setBigDecimal(2, amount);
            statement.setString(3, currency.name());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public Account getAccountById(long accountId) throws AccountNotFoundException {
        return DaoUtils.executeQuery(datasource, FIND_ACCOUNT_BY_ID, statement -> {
            statement.setLong(1, accountId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Currency currency = Currency.from(rs.getString("CURRENCY"));
                    Timestamp timestamp = rs.getTimestamp("CREATE_TIME");
                    LocalDateTime createTime = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
                    BigDecimal amount = rs.getBigDecimal("AMOUNT");
                    String fullname = rs.getString("FULLNAME");
                    return new Account(accountId, fullname, amount, currency, createTime);
                } else {
                    return null;
                }
            }
        });
    }
}
