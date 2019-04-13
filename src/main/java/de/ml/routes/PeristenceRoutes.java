package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.endpoints.PersistenceEndpoints;
import de.ml.processors.CountFetch.CountFetchProcessor;
import de.ml.processors.UpdateImageDB.UpdateImageDBProcessor;

public class PeristenceRoutes extends RouteBuilder {

    public static final String UPDATE_ALL = "direct:update-all";
    public static final String COUNT_FETCH = "direct:count-fetch";
    public static final String MONGO_UPDATE_ALL_ROUTEID = "mongoUpdateAll";
    public static final String MONGO_COUNT_FETCH_ROUTEID = "mongoCountFetch";
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
            from(UPDATE_ALL)
                    .routeId( MONGO_UPDATE_ALL_ROUTEID )
                    .autoStartup( false )
                    .log("start updating data base")
                    .split().body()
                    .parallelProcessing()
                    .process(updateProcessor)
                    .to(persistenceEndpoints.updateImage())
                    .end()// end split
                    .log("update db finished");
            from(COUNT_FETCH)
                    .routeId( MONGO_COUNT_FETCH_ROUTEID )
                    .autoStartup( false )
                    .process(countFetchProcessor)
                    .to(persistenceEndpoints.updateImage())
                    .log(LoggingLevel.DEBUG, "Counted fetch.");


    }
}
