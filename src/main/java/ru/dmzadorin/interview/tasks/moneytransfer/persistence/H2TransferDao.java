package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class H2TransferDao implements TransferDao {
    private static final String CREATE_TRANSFER =
            "insert into transfer (SOURCE_ID, RECIPIENT_ID, AMOUNT, CURRENCY, CREATE_TIME) values (?,?,?,?, CURRENT_TIMESTAMP())";
    private static final String SUBTRACT_AMOUNT = "update account set amount = amount - ? where id = ?";
    private static final String ADD_AMOUNT = "update account set amount = amount + ? where id = ?";
    private static final String GET_ALL_TRANSFERS = "select * from transfer";
    private final DataSource datasource;

    @Inject
    public H2TransferDao(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public Transfer transferAmount(long sourceId, long recipientId, BigDecimal amount, Currency currency) {
        return DaoUtils.executeQueryWithTransaction(datasource, conn -> {
            Transfer transfer;
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_TRANSFER, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, sourceId);
                stmt.setLong(2, recipientId);
                stmt.setBigDecimal(3, amount);
                stmt.setString(4, currency.name());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        updateAccount(sourceId, amount, SUBTRACT_AMOUNT, conn);
                        updateAccount(recipientId, amount, ADD_AMOUNT, conn);
                        transfer = new Transfer(id, sourceId, recipientId, amount, currency);
                    } else {
                        throw new SQLException("No keys generated!");
                    }
                }
            }
            return transfer;
        });
    }

    @Override
    public Collection<Transfer> getTransfers() {
        return DaoUtils.executeQuery(datasource, GET_ALL_TRANSFERS, statement -> {
            List<Transfer> transfers = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("ID");
                    long sourceId = rs.getLong("SOURCE_ID");
                    long recipientId = rs.getLong("RECIPIENT_ID");
                    BigDecimal amount = rs.getBigDecimal("AMOUNT");
                    Currency currency = Currency.from(rs.getString("CURRENCY"));
                    transfers.add(new Transfer(id, sourceId, recipientId, amount, currency));
                }
            }
            return transfers;
        });
    }

    private void updateAccount(long accountId, BigDecimal amount, String query, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountId);
            statement.executeUpdate();
        }
    }
}
