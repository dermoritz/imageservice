package de.ml.persitence;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

import de.ml.routes.PeristenceRoutes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoAliveListener implements ServerMonitorListener {

    private final ProducerTemplate template;

    @Inject
    public MongoAliveListener( CamelContext context ) {
        template = context.createProducerTemplate();
    }

    @Override
    public void serverHearbeatStarted( ServerHeartbeatStartedEvent serverHeartbeatStartedEvent ) {
        log.debug( "Checking Mongo..." );
    }

    @Override
    public void serverHeartbeatSucceeded( ServerHeartbeatSucceededEvent serverHeartbeatSucceededEvent ) {
        log.debug( "... Mongo is alive :-)." );
        String status = getStatus();
        if( status.equalsIgnoreCase( "stopped" ) ) {
            log.info( "Starting Mongo routes..." );
            sendStatus( "start" );
        } else if( status.equalsIgnoreCase( "suspended" ) ) {
            log.info( "Resuming Mongo routes..." );
            sendStatus( "resume" );
        }

    }

    @Override
    public void serverHeartbeatFailed( ServerHeartbeatFailedEvent serverHeartbeatFailedEvent ) {
        log.warn( "... Mongo is dead." );
        String status = getStatus();
        if( status.equalsIgnoreCase( "started" ) ) {
            log.info( "Suspending mongo routes..." );
            sendStatus( "suspend" );
        }
    }

    private String getStatus() {
        String statusUpdate =
                template.requestBody( "controlbus:route?routeId=" + PeristenceRoutes.MONGO_UPDATE_ALL_ROUTEID + "&action=status&loggingLevel=DEBUG", null,
                        String.class );

        String statusFetchCount =
                template.requestBody( "controlbus:route?routeId=" + PeristenceRoutes.MONGO_COUNT_FETCH_ROUTEID + "&action=status&loggingLevel=DEBUG", null,
                        String.class );
        if( statusFetchCount != null && !statusFetchCount.equals( statusUpdate ) ) {
            throw new IllegalStateException( "Status of all mongo routes should be the same." );
        }
        return statusFetchCount == null ? "unknown" : statusFetchCount;
    }

    private void sendStatus( String status ) {
        template.sendBody(
                "controlbus:route?routeId=" + PeristenceRoutes.MONGO_UPDATE_ALL_ROUTEID + "&action=" + status + "&loggingLevel=DEBUG",
                null );
        template.sendBody(
                "controlbus:route?routeId=" + PeristenceRoutes.MONGO_COUNT_FETCH_ROUTEID + "&action=" + status + "&loggingLevel=DEBUG",
                null );
    }

}
