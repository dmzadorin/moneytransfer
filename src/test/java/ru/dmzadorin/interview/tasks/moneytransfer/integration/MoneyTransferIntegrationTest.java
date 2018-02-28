package ru.dmzadorin.interview.tasks.moneytransfer.integration;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import ru.dmzadorin.interview.tasks.moneytransfer.config.MoneyTransferApplication;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.json.ErrorMessage;
import ru.dmzadorin.interview.tasks.moneytransfer.model.request.AccountCreateRequest;
import ru.dmzadorin.interview.tasks.moneytransfer.model.request.MoneyTransferRequest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class MoneyTransferIntegrationTest extends JerseyTest {

    private static final String CREATE_ACCOUNT_PATH = "moneyTransfer/createAccount";
    private static final String GET_ACCOUNT_PATH = "moneyTransfer/getAccount";
    private static final String TRANSFER_MONEY_PATH = "moneyTransfer/transferMoney";

    @Override
    protected Application configure() {
        return new MoneyTransferApplication();
    }

    @Test
    public void testPing() {
        Response response = target("moneyTransfer/ping").request().get();
        String res = response.readEntity(String.class);
        assertEquals("pong", res);
        response.close();
    }

    @Test
    public void testCreateAccount() {
        AccountCreateRequest entity = new AccountCreateRequest();
        entity.setFullName("test");
        entity.setCurrency("EUR");
        entity.setInitialBalance(10.0);
        sendRequest(entity, CREATE_ACCOUNT_PATH, Account.class, account -> {
            assertEquals(entity.getFullName(), account.getFullName());
            assertEquals(Currency.EUR, account.getCurrency());
            BigDecimal expectedAmount = BigDecimal.valueOf(entity.getInitialBalance()).setScale(2, BigDecimal.ROUND_UP);
            assertEquals(expectedAmount, account.getAmount());
        });
        entity.setInitialBalance(-1.0);
        sendRequest(entity, CREATE_ACCOUNT_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Account initial balance cannot be less then zero, value: " + entity.getInitialBalance(), errorMessage.getMessage());
        });
        entity.setCurrency("TEST");
        entity.setInitialBalance(100);
        sendRequest(entity, CREATE_ACCOUNT_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Currency TEST is not supported!", errorMessage.getMessage());
        });
    }

    @Test
    public void testGetAccount() {
        Account expectedAccount = createAccount("test", "GBP", 10.0);
        Account actualAccount = getAccount(expectedAccount.getId());
        assertEquals(expectedAccount.getFullName(), actualAccount.getFullName());
        assertEquals(expectedAccount.getCurrency(), actualAccount.getCurrency());
        assertEquals(expectedAccount.getAmount(), actualAccount.getAmount());
    }

    @Test
    public void testTransferMoney() {
        Account firstAccount = createAccount("test", "EUR", 1000.0);
        Account secondAccount = createAccount("test2", "EUR", 500.0);
        long firstAccountId = firstAccount.getId();
        long secondAccountId = secondAccount.getId();

        MoneyTransferRequest entity = new MoneyTransferRequest();
        entity.setSourceAccount(firstAccount.getId());
        entity.setRecipientAccount(secondAccountId);
        entity.setCurrency("EUR");
        entity.setAmount(100.0);

        sendRequest(entity, TRANSFER_MONEY_PATH, Transfer.class, transfer -> {
            assertEquals(entity.getSourceAccount(), transfer.getSourceAccountId());
            assertEquals(entity.getRecipientAccount(), transfer.getRecipientAccountId());
            assertEquals(Currency.EUR, transfer.getCurrency());
            BigDecimal expectedAmount = BigDecimal.valueOf(entity.getAmount()).setScale(2, BigDecimal.ROUND_UP);
            assertEquals(expectedAmount, transfer.getAmount());
        });

        entity.setCurrency("USD");
        sendRequest(entity, TRANSFER_MONEY_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Source currency EUR does not match transfer currency USD", errorMessage.getMessage());
        });

        Account thirdAccount = createAccount("test3", "USD", 500.0);
        entity.setRecipientAccount(thirdAccount.getId());
        entity.setCurrency("EUR");
        sendRequest(entity, TRANSFER_MONEY_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Source currency EUR does not match recipient currency USD", errorMessage.getMessage());
        });

        entity.setAmount(1000.0);
        entity.setRecipientAccount(secondAccountId);
        sendRequest(entity, TRANSFER_MONEY_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Not enough funds on account id " + firstAccountId + ", current amount = 900.00, transfer amount = 1000.0", errorMessage.getMessage());
        });


        entity.setSourceAccount(0);
        sendRequest(entity, TRANSFER_MONEY_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Account with id 0 not found in storage", errorMessage.getMessage());
        });

        entity.setRecipientAccount(-1);
        entity.setSourceAccount(firstAccountId);

        sendRequest(entity, TRANSFER_MONEY_PATH, ErrorMessage.class, errorMessage -> {
            assertEquals("Account with id -1 not found in storage", errorMessage.getMessage());
        });
    }

    private Account createAccount(String fullName, String currency, double initialBalance) {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setFullName(fullName);
        accountCreateRequest.setCurrency(currency);
        accountCreateRequest.setInitialBalance(initialBalance);
        Entity request = Entity.entity(accountCreateRequest, MediaType.APPLICATION_JSON_TYPE);
        Response response = target(CREATE_ACCOUNT_PATH).request(MediaType.APPLICATION_JSON_TYPE).post(request);
        return response.readEntity(Account.class);
    }

    private Account getAccount(long id) {
        Response response = target(GET_ACCOUNT_PATH).queryParam("accountId", id).request().get();
        return response.readEntity(Account.class);
    }

    private <T, R> void sendRequest(R requestEntity, String path, Class<T> entityClass, Consumer<T> responseConsumer) {
        Entity request = Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE);
        Response response = target(path).request(MediaType.APPLICATION_JSON_TYPE).post(request);
        T result = response.readEntity(entityClass);
        try {
            responseConsumer.accept(result);
        } finally {
            response.close();
        }
    }
}
