package ru.dmzadorin.interview.tasks.moneytransfer.service

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.DifferentCurrenciesException
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.InsufficientFundsException
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.AccountDao
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.TransferDao
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode
import java.time.LocalDateTime

class MoneyTransferServiceImplTest extends Specification {
    @Shared
    AccountDao accountDao

    @Shared
    TransferDao transferDao

    @Shared
    MoneyTransferService service

    void setup() {
        accountDao = Mock(AccountDao)
        transferDao = Mock(TransferDao)
        service = new MoneyTransferServiceImpl(accountDao, transferDao)
    }

    def "Verify account is properly saved"() {
        when:
        def account = service.createAccount('name', amount, currecny)
        then:
        1 * accountDao.createAccount(fullName, amount, currecny) >> expectedAccount
        account == expectedAccount
        where:
        fullName = 'name'
        amount = getDecimal(100)
        currecny = Currency.EUR
        expectedAccount = new Account(1, fullName, amount, currecny, LocalDateTime.now())
    }

    def "Verify that account is properly retrieved by id"() {
        when:
        def account = service.getAccountById(id)
        then:
        1 * accountDao.getAccountById(id) >> expectedAccount
        account == expectedAccount
        where:
        id = 1
        expectedAccount = new Account(id, 'name', getDecimal(100), Currency.EUR, LocalDateTime.now())
    }

    def "Test withdraw amount"() {
        when:
        def transfer = service.transferMoney(sourceAccountId, recipientAccountId, amount, currency)
        then:
        1 * accountDao.getAccountById(sourceAccountId) >> sourceAccount
        1 * accountDao.getAccountById(recipientAccountId) >> recipientAccount
        1 * transferDao.transferAmount(sourceAccountId, recipientAccountId, amount, currency) >> expectedTransfer
        transfer == expectedTransfer
        where:
        sourceAccountId = 1
        recipientAccountId = 2
        amount = getDecimal(100)
        currency = Currency.EUR
        sourceAccount = new Account(sourceAccountId, 'name', getDecimal(100), Currency.EUR, LocalDateTime.now())
        recipientAccount = new Account(recipientAccountId, 'name2', getDecimal(200), Currency.EUR, LocalDateTime.now())
        expectedTransfer = new Transfer(1, sourceAccountId, recipientAccountId, amount, currency, LocalDateTime.now())
    }

    @Unroll
    def "verify that validation throws #exceptionClass"() {
        when:
        service.transferMoney(sourceAccount.id, recipientAccount.id, amount, currency)
        then:
        1 * accountDao.getAccountById(sourceAccount.id) >> sourceAccount
        1 * accountDao.getAccountById(recipientAccount.id) >> recipientAccount
        thrown(exceptionClass)
        where:
        amount << [getDecimal(100), getDecimal(100), getDecimal(200)]
        currency << [Currency.USD, Currency.EUR, Currency.EUR]
        sourceAccount << [
                new Account(1, 'name', getDecimal(100), Currency.EUR, LocalDateTime.now()),
                new Account(1, 'name', getDecimal(100), Currency.USD, LocalDateTime.now()),
                new Account(1, 'name', getDecimal(100), Currency.EUR, LocalDateTime.now()),
        ]
        recipientAccount << [
                new Account(2, 'name2', getDecimal(200), Currency.EUR, LocalDateTime.now()),
                new Account(2, 'name2', getDecimal(200), Currency.EUR, LocalDateTime.now()),
                new Account(2, 'name2', getDecimal(200), Currency.EUR, LocalDateTime.now()),
        ]
        exceptionClass << [DifferentCurrenciesException, DifferentCurrenciesException, InsufficientFundsException]
    }

    def "Verify that all transfers are properly retrieved"() {
        when:
        def transfers = service.getAllTransfers()
        then:
        1 * transferDao.getTransfers() >> expectedTransfers
        transfers == expectedTransfers
        where:
        expectedTransfers = [
                new Transfer(1, 1, 2, getDecimal(100), Currency.EUR, LocalDateTime.now()),
                new Transfer(2, 2, 1, getDecimal(100), Currency.EUR, LocalDateTime.now()),
        ]
    }

    def "Verify that transfer is properly retrieved by id"() {
        when:
        def transfer = service.getTransferById(id)
        then:
        1 * transferDao.getTransferById(id) >> expectedTransfer
        transfer == expectedTransfer
        where:
        id = 1
        expectedTransfer = new Transfer(id, 1, 2, getDecimal(100), Currency.EUR, LocalDateTime.now())
    }

    static BigDecimal getDecimal(double v) {
        BigDecimal.valueOf(v).setScale(2, RoundingMode.CEILING)
    }
}
