package ru.dmzadorin.interview.tasks.moneytransfer.config;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.BaseExceptionMapper;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.AccountDao;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.H2AccountDao;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.H2TransferDao;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.TransferDao;
import ru.dmzadorin.interview.tasks.moneytransfer.service.MoneyTransferController;
import ru.dmzadorin.interview.tasks.moneytransfer.service.MoneyTransferService;
import ru.dmzadorin.interview.tasks.moneytransfer.service.MoneyTransferServiceImpl;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class MoneyTransferApplication extends ResourceConfig {

    public MoneyTransferApplication() {
        String url = System.getProperty("dbUrl", "jdbc:h2:mem:moneytransfer;DB_CLOSE_DELAY=-1");
        String user = System.getProperty("dbUser", "sa");
        String pass = System.getProperty("dbPass", "sa");
        String initScriptPath = System.getProperty("initScriptPath", "classpath:/database/schema.sql");
        DataSource ds = initDataSource(url, user, pass, initScriptPath);
        register(new DependencyBinder(ds));
        register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
        register(org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider.class);
        register(BaseExceptionMapper.class);
        registerClasses(MoneyTransferController.class);
    }

    private DataSource initDataSource(String url, String user, String pass, String initScriptPath) {
        try {
            RunScript.execute(url, user, pass, initScriptPath, StandardCharsets.UTF_8, false);
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(pass);
            return ds;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class DependencyBinder extends AbstractBinder {
        private final DataSource ds;

        DependencyBinder(DataSource ds) {
            this.ds = ds;
        }

        @Override
        protected void configure() {
            bind(MoneyTransferServiceImpl.class).to(MoneyTransferService.class).in(Singleton.class);
            bind(H2AccountDao.class).to(AccountDao.class).in(Singleton.class);
            bind(H2TransferDao.class).to(TransferDao.class).in(Singleton.class);
            bind(ds).to(DataSource.class).in(Singleton.class);
        }
    }
}
