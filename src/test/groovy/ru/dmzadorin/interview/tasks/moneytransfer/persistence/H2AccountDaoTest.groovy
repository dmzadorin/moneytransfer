package ru.dmzadorin.interview.tasks.moneytransfer.persistence

import ru.dmzadorin.interview.tasks.moneytransfer.config.DataSourceConfigurer
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class H2AccountDaoTest extends Specification {
    @Shared
    AccountDao accountDao

    void setupSpec() {
        def ds = DataSourceConfigurer.initDataSource("jdbc:h2:mem:moneytransfer;DB_CLOSE_DELAY=-1", "sa", "",
                "classpath:/database/schema.sql")
        accountDao = new H2AccountDao(ds)
    }

    @Unroll
    def "verify that account '#fullName' is saved properly and get by id"() {
        when:
        def id = accountDao.saveAccount(fullName, amount, currency)
        then:
        noExceptionThrown()
        id != null
        def account = accountDao.getAccountById(id)
        account != null
        account.fullName == fullName
        account.currency == currency
        account.amount == amount
        where:
        fullName | amount                    | currency
        'test'   | BigDecimal.valueOf(100.0) | Currency.EUR
        'test2'  | BigDecimal.valueOf(10.0)  | Currency.USD
        'test3'  | BigDecimal.valueOf(20.0)  | Currency.GBP
    }

    @Unroll
    def "verify that amount for account '#fullName' is updated properly"() {
        given:
        def id = accountDao.saveAccount(fullName, amount, currency)
        when:
        accountDao.updateAmount(id, updatedAmount)
        then:
        noExceptionThrown()
        def account = accountDao.getAccountById(id)
        account != null
        account.fullName == fullName
        account.currency == currency
        account.amount == updatedAmount
        where:
        fullName     | amount                    | currency     | updatedAmount
        'amountTest' | BigDecimal.valueOf(100.0) | Currency.EUR | BigDecimal.valueOf(50.0)
    }
}
