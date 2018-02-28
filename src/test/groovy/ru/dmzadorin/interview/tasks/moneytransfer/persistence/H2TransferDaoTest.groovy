package ru.dmzadorin.interview.tasks.moneytransfer.persistence

import ru.dmzadorin.interview.tasks.moneytransfer.config.DataSourceConfigurer
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode

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
        transferDao = new H2TransferDao(ds)
        source = accountDao.createAccount('fullName', BigDecimal.valueOf(1000.0), Currency.USD)
        recipient = accountDao.createAccount('fullName', BigDecimal.valueOf(500.0), Currency.USD)
    }

    @Unroll
    def "Create transfer (#transferAmount #currency) and verify amounts"() {
        when:
        def transfer = transferDao.transferAmount(sourceAccountId, recipientAccountId, transferAmount, currency)
        then:
        noExceptionThrown()
        transfer.amount == transferAmount
        transfer.currency == currency
        transfer.sourceAccountId == sourceAccountId
        transfer.recipientAccountId == recipientAccountId

        def actualSource = accountDao.getAccountById(sourceAccountId)
        def actualRecipient = accountDao.getAccountById(recipientAccountId)
        actualSource.amount == expectedSourceAmt
        actualRecipient.amount == expectedRecipientAmt
        where:
        sourceAccountId   | recipientAccountId | expectedSourceAmt | expectedRecipientAmt | transferAmount  | currency
        source.getId()    | recipient.getId()  | getDecimal(900)   | getDecimal(600)      | getDecimal(100) | Currency.USD
        recipient.getId() | source.getId()     | getDecimal(550)   | getDecimal(950)      | getDecimal(50)  | Currency.USD
    }

    @Unroll
    def "Verify that we have #transfersCount transfers in db"() {
        expect:
        transferDao.getTransfers().size() == transfersCount
        where:
        transfersCount = 2
    }

    static BigDecimal getDecimal(double v) {
        BigDecimal.valueOf(v).setScale(2, RoundingMode.UP)
    }
}
