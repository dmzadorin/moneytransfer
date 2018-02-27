package ru.dmzadorin.interview.tasks.moneytransfer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.CurrencyNotSupportedException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.request.AccountCreateRequest;
import ru.dmzadorin.interview.tasks.moneytransfer.model.request.MoneyTransferRequest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
@Path("/moneyTransfer")
public class MoneyTransferController {
    private static final Logger logger = LogManager.getLogger();

    @Inject
    private MoneyTransferService moneyTransferService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transferMoney")
    public Transfer transferMoney(MoneyTransferRequest request) {
        BigDecimal decimalAmount = new BigDecimal(request.getAmount());
        Currency currency = Currency.from(request.getCurrency());
        return moneyTransferService.withdrawAmount(request.getSourceAccount(), request.getTargetAccount(), decimalAmount, currency);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createAccount")
    public Account createAccount(AccountCreateRequest accountCreateRequest) {
        Currency curr = Currency.from(accountCreateRequest.getCurrency());
        if (curr == Currency.NOT_PRESENT) {
            throw new CurrencyNotSupportedException(accountCreateRequest.getCurrency());
        }
        return moneyTransferService.saveAccount(
                accountCreateRequest.getFullName(),
                new BigDecimal(accountCreateRequest.getInitialBalance()),
                curr);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/account")
    public Account getAccount(@QueryParam(value = "accountId") long accountId) {
        return moneyTransferService.getAccountById(accountId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getTransfers")
    public Collection<Transfer> getAllTransfers() {
        return moneyTransferService.getAllTransfers();
    }

}
