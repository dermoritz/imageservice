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
import org.apache.camel.component.timer.TimerEndpoint;

public class TriggerEndpointProvider {

    private CamelContext context;

    @Inject
    private TriggerEndpointProvider(CamelContext context){
        this.context = context;

    }

    private final static int TWENTY_FOUR_HOURS = 24 * 3600000;

    @Produces
    @TriggerEndpoint
    private Endpoint getTriggerEndpoint(){
        TimerEndpoint timer = context.getEndpoint("timer:updateAll", TimerEndpoint.class);
        timer.setDelay(TWENTY_FOUR_HOURS);
        timer.setPeriod(TWENTY_FOUR_HOURS);
        return timer;

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface TriggerEndpoint {

    }
}
