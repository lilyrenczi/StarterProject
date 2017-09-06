package org.czi.kb.service_example.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.czi.rato.api.cluster.service.InitializationContext;
import org.czi.rato.api.cluster.service.MetaService;
import org.czi.rato.api.cluster.service.MetaServiceContext;
import org.czi.rato.api.cluster.service.MetaServiceInfo;
import org.czi.rato.api.cluster.service.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * a Template for a new service in RATO
 * Created by amradawi on 2016-11-11.
 */
@MetaServiceInfo(majorVersion = 0,
        minorVersion = 1,
        description = "A new Service template in Rato")
@SpringBootApplication
@ComponentScan({"org.czi.kb.service_example.testing.config","org.czi.kb.service_example.example"}) //change only example, NOT test config
public class ServiceExample implements MetaService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExample.class);
    private final Collection<Ticket> ticketCollection = new ArrayList<>();
    private CountDownLatch latch = null;
    private boolean ranDoService = false;
    private boolean ranInit = false;
    private boolean ranDestroy = false;

    // Internal dependency example
    private final ExampleComponent exampleComponent;

    @Autowired
    public ServiceExample(final ExampleComponent exampleComponent) {
        this.exampleComponent = exampleComponent;
    }

    /**
     * Main class place holder for spring boot.
     * @param args run arguments
     */
    public static void main(final String[] args) {
        logger.info("MAIN");
    }

    /**
     * Interface for other services to invoke with tickets.
     * @param context information about the process
     */
    @Override
    public void doService(final MetaServiceContext context) {
        logger.info("Running DoService");
        ranDoService = true;

        // move logic to supporting class to reduce the logic used here
        try {
            exampleComponent.linkCitation(context);
        } catch (final NullPointerException exception) {
            // example of exception handling
            logger.error("Object Not Found!", exception);
        }
    }

    /**
     * Initialize service.
     * @param context information provided to the service
     */
    @Override
    public void init(final InitializationContext context) {
        ranInit = true;
        logger.info("Running Init");

        // example of registering event listening,
        // discouraged since services should use tickets
        context.registerServiceEvent(command -> {
            final Map<String, Object> parameters = command.getParameters();
            parameters.forEach((k,v) -> logger.info("{} : {}", k, v));
            context.setStatusMessage("received command from event bus");
        });
    }

    /**
     * Destroy service.
     * @param context information provided to the service
     */
    @Override
    public void destroy(final InitializationContext context) {
        ranDestroy = true;
        logger.info("Running Destroy");
    }

    // the following methods are provided for other services to check progress of this service
    // not necessarily implemented as of now but would be nice if they are
    public boolean isRanDoService() {
        return ranDoService;
    }

    public boolean isRanInit() {
        return ranInit;
    }

    public boolean isRanDestroy() {
        return ranDestroy;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(final CountDownLatch latch) {
        this.latch = latch;
    }

    public Collection<Ticket> getTicketCollection() {
        return ticketCollection;
    }

}
