package ru.dmzadorin.interview.tasks.moneytransfer.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import ru.dmzadorin.interview.tasks.moneytransfer.config.MoneyTransferApplication;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class App {
    private static final Logger logger = LogManager.getLogger();
    private static final int DEFAULT_PORT_VALUE = 9998;

    public static void main(String[] args) {
        int port;
        if (args == null || args.length != 1) {
            System.out.println("Using default port value: 9998");
            port = DEFAULT_PORT_VALUE;
        } else {
            port = parseInt(args[0]);
            if (port == -1) {
                return;
            }
        }
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
        MoneyTransferApplication config = new MoneyTransferApplication();
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        try {
            server.start();
            logger.info("Press any key to stop the server...");
            System.in.read();
            server.shutdown();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect port value, should be integer = " + value);
        }
        return -1;
    }
}