package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.endpoints.TriggerEndpointProvider.TriggerEndpoint;
import de.ml.image.ImageFromFolder.ImageProviderImpl;

public class UpdateImages extends RouteBuilder {

    private Processor update;
    private Endpoint updateTrigger;

    @Inject
    private UpdateImages(@ImageProviderImpl Processor update, @TriggerEndpoint Endpoint updateTrigger) {
        this.update = update;
        this.updateTrigger = updateTrigger;
    }

    @Override
    public void configure() throws Exception {
        from(updateTrigger).process(update);
    }

}
