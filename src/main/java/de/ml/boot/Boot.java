package de.ml.boot;

import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.impl.SimpleRegistry;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.events.ContainerShutdown;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.base.Preconditions;

import de.ml.boot.AllowedUserProvider.AllowedUsers;
import de.ml.routes.RestRoute;

@Singleton
public class Boot {

    public static final String REALM_REG_KEY = "realm";

    @Inject
    private Logger log;

    private CamelContext context;

    private Instance<RouteBuilder> routes;

    private CamelMain main;

    private Map<String, String> realm;

    private SimpleRegistry registry;

    @Inject
    private Boot(CamelContext context, @Any Instance<RouteBuilder> routes, CamelMain main, @AllowedUsers Map<String, String> realm, SimpleRegistry registry) throws Exception {
        this.realm = realm;
        this.registry = registry;
        // eliminates logging to java.util.logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // redirects all java.util.logger stuff to slf4j
        SLF4JBridgeHandler.install();
        this.routes = routes;
        this.main = main;
        this.context = Preconditions.checkNotNull(context);
        setupContext();
    }

    private void setupContext() throws Exception {
        context.addComponent("restlet", new RestletComponent());
        registry.put(REALM_REG_KEY, realm);
        for (RouteBuilder routeBuilder : routes) {
            context.addRoutes(routeBuilder);
        }

    }

    public void start(@Observes ContainerInitialized event) throws Exception {
        log.info("starting");
        main.run();
    }

    public void stop(@Observes ContainerShutdown event) throws Exception {
        log.info("starting");
        main.stop();
    }

}
