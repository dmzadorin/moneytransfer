package ru.dmzadorin.interview.tasks.moneytransfer.persistence

import ru.dmzadorin.interview.tasks.moneytransfer.config.DataSourceConfigurer
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency
import spock.lang.Shared
import spock.lang.Specification

class H2TransferDaoTest extends Specification {
    @Shared
    AccountDao accountDao
    @Shared
    TransferDao transferDao

    @Shared
    Account source

    @Shared
    Account recipient

    void setupSpec() {
        def ds = DataSourceConfigurer.initDataSource("jdbc:h2:mem:moneytransfer;DB_CLOSE_DELAY=-1", "sa", "",
                "classpath:/database/schema.sql")
        accountDao = new H2AccountDao(ds)
        transferDao = new H2TransferDao(ds, accountDao)
        def sourceId = accountDao.saveAccount('fullName', BigDecimal.valueOf(1000.0), Currency.USD)
        def recipientId = accountDao.saveAccount('fullName', BigDecimal.valueOf(500.0), Currency.USD)
        source = accountDao.getAccountById(sourceId)
        recipient = accountDao.getAccountById(recipientId)
    }

    def "TransferAmount"() {
        when:
        def expectedSourceAmt = source.amount.subtract(transferAmount)
        def expectedRecipientAmt = recipient.amount.add(transferAmount)
        def transfer = transferDao.transferAmount(source, recipient, transferAmount, currency)
        then:
        transfer != null
        source.amount == expectedSourceAmt
        recipient.amount == expectedRecipientAmt
        where:
        transferAmount            | currency
        BigDecimal.valueOf(100.0) | Currency.USD
    }

    def "GetTransfers"() {
    }
}
