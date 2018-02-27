package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class H2TransferDao implements TransferDao {
    private static final String CREATE_TRANSFER =
            "insert into transfer (SOURCE_ID, RECIPIENT_ID, AMOUNT, CURRENCY, CREATE_TIME) values (?,?,?,?, CURRENT_TIMESTAMP())";
    private static final String UPDATE_ACCOUNT = "update account set amount = ? where id = ?";
    private static final String GET_ALL_TRANSFERS = "select * from transfer";
    private final DataSource datasource;
    private final AccountDao accountDao;

    @Inject
    public H2TransferDao(DataSource datasource, AccountDao accountDao) {
        this.datasource = datasource;
        this.accountDao = accountDao;
    }

    @Override
    public Transfer transferAmount(Account source, Account recipient, BigDecimal amount, Currency currency) {
        return DaoUtils.executeQueryWithTransaction(datasource, CREATE_TRANSFER, statement -> {
            statement.setLong(1, source.getId());
            statement.setLong(2, recipient.getId());
            statement.setBigDecimal(3, amount);
            statement.setString(4, currency.name());
            Transfer transfer = null;
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    source.executeWithdraw(recipient, amount);
                    accountDao.updateAmount(source.getId(), source.getAmount());
                    accountDao.updateAmount(recipient.getId(), recipient.getAmount());
                    transfer = new Transfer(id, source.getId(), recipient.getId(), amount, source.getCurrency());
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
}
