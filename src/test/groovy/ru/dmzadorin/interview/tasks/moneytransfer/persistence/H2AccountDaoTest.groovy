package ru.dmzadorin.interview.tasks.moneytransfer.persistence

import org.junit.Assert
import ru.dmzadorin.interview.tasks.moneytransfer.config.DataSourceConfigurer
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.EntityNotFoundException
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
        def account = accountDao.createAccount(fullName, amount, currency)
        then:
        noExceptionThrown()
        account.fullName == fullName
        account.currency == currency
        account.amount == amount
        where:
        fullName | amount           | currency
        'test'   | getDecimal(100)  | Currency.EUR
        'test2'  | getDecimal(10.0) | Currency.USD
        'test3'  | getDecimal(20.0) | Currency.GBP
    }

    @Unroll
    def "verify that there is no account with id '#id'"() {
        when:
        accountDao.getAccountById(id)
        then:
        thrown(EntityNotFoundException)
        where:
        id = 0
    }

    static BigDecimal getDecimal(double v) {
        BigDecimal.valueOf(v).setScale(2)
    }

}
