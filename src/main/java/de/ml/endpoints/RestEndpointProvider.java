package de.ml.endpoints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

public class RestEndpointProvider {

    private CamelContext context;

    @Inject
    private RestEndpointProvider(CamelContext context){
        this.context = context;
    }

    @Produces
    @RandomImageEndpoint
    private Endpoint getRestFileEndpoint() {
        org.apache.camel.component.rest.RestEndpoint endpoint = context.getEndpoint("rest:get:next",
                                                                                    org.apache.camel.component.rest.RestEndpoint.class);
        return endpoint;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface RandomImageEndpoint {

    }
}
