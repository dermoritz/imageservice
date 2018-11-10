package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.endpoints.PersistenceEndpoints;
import de.ml.processors.CountFetch.CountFetchProcessor;
import de.ml.processors.UpdateImageDB.UpdateImageDBProcessor;

public class PeristenceRoutes extends RouteBuilder {

    public static final String UPDATE_ALL = "direct:update-all";
    public static final String COUNT_FETCH = "direct:count-fetch";
    private Processor updateProcessor;
    private final PersistenceEndpoints persistenceEndpoints;
    private Processor countFetchProcessor;

    @Inject
    public PeristenceRoutes(@UpdateImageDBProcessor Processor updateProcessor, @CountFetchProcessor Processor countFetchProcessor, PersistenceEndpoints persistenceEndpoints) {
        this.updateProcessor = updateProcessor;
        this.countFetchProcessor = countFetchProcessor;
        this.persistenceEndpoints = persistenceEndpoints;
    }

    @Override
    public void configure() {
        if (persistenceEndpoints.isPersistenceRunning()) {
            from(UPDATE_ALL)
                    .log("start updating data base")
                    .split().body()
                    .parallelProcessing()
                    .process(updateProcessor)
                    .to(persistenceEndpoints.updateImage())
                    .end()// end split
                    .log("update db finished");
            from(COUNT_FETCH)
                    .process(countFetchProcessor)
                    .to(persistenceEndpoints.updateImage())
                    .log(LoggingLevel.DEBUG, "Counted fetch.");
        } else {
            from(UPDATE_ALL).filter(exchange -> false).log("afas");
            from(COUNT_FETCH).filter(exchange -> false).log("asf");
        }

    }
}
