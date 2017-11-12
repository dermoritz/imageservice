package de.ml.boot;

import java.util.Map;
import java.util.concurrent.Executors;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.events.ContainerShutdown;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.base.Preconditions;

import de.ml.boot.AllowedUserProvider.AllowedUsers;

@Singleton
public class Boot {

    public static final String REALM_REG_KEY = "realm";

    private Logger log;

    private CamelContext context;

    private Instance<RouteBuilder> routes;

    private CamelMain main;

    private Map<String, String> realm;

    private SimpleRegistry registry;

    @Inject
    private Boot( CamelContext context, @Any Instance<RouteBuilder> routes, CamelMain main,
            @AllowedUsers Map<String, String> realm, SimpleRegistry registry, Logger log ) throws Exception {
        this.realm = realm;
        this.registry = registry;
        this.log = log;
        // eliminates logging to java.util.logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // redirects all java.util.logger stuff to slf4j
        SLF4JBridgeHandler.install();

        this.routes = routes;
        this.main = main;
        this.context = Preconditions.checkNotNull( context );
        setupContext();
    }

    private void setupContext() throws Exception {
        registerUsers();
        for( RouteBuilder routeBuilder : routes ) {
            context.addRoutes( routeBuilder );
        }

    }

    private void registerUsers() {
        if( !realm.isEmpty() ) {
            registry.put( REALM_REG_KEY, realm );
            for( Map.Entry<String, String> user : realm.entrySet() ) {
                log.info( "User " + user.getKey() + ":" + user.getValue() + " is allowed to access." );
            }
        }
        else {
            log.info("no users set, access is allowed for all.");
        }

    }

    public void start( @Observes ContainerInitialized event ) throws Exception {
        log.info( "starting" );
        main.run();
    }

}
