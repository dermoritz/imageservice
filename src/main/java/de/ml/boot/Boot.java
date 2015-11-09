package de.ml.boot;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.impl.SimpleRegistry;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.events.ContainerShutdown;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

@Singleton
public class Boot {

	public static final String DB = "db";

    @Inject
	private Logger log;

	private CamelContext context;

    private Instance<RouteBuilder> routes;

    private CamelMain main;


	@Inject
	private Boot(CamelContext context, @Any Instance<RouteBuilder> routes, CamelMain main) throws Exception{
		this.routes = routes;
        this.main = main;
        this.context = Preconditions.checkNotNull(context);
        setupContext();
	}

	private void setupContext() throws Exception {
	    context.addComponent("restlet", new RestletComponent());
        for (RouteBuilder routeBuilder : routes) {
            context.addRoutes(routeBuilder);
        }

    }

    public void start(@Observes ContainerInitialized event) throws Exception{
		log.info("starting");
		main.run();
	}

	public void stop(@Observes ContainerShutdown event) throws Exception{
	    main.stop();
	}

}
