package de.ml.boot;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

public class CamelContextProvider {

    private SimpleRegistry registry;

    @Inject
    private CamelContextProvider(SimpleRegistry registry) {
        this.registry = registry;

    }

    @Produces
    @Singleton
    private CamelContext getContext() {
        return new DefaultCamelContext(registry);
    }
}
