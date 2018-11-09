package de.ml.routes;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.Endpoint;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.endpoints.TriggerEndpointProvider.TriggerEndpoint;
import de.ml.image.ImageFromFolder;
import de.ml.image.ImageFromFolder.ImageProviderImpl;

public class UpdateImages extends RouteBuilder {

    public static final String UPDATE_PLUG = "direct:updateAll";
    private final Processor unzip;
    private Processor update;
    private Endpoint updateTrigger;

    @Inject
    private UpdateImages(@ImageProviderImpl Processor update, @TriggerEndpoint Endpoint updateTrigger, @Named("unzip") Processor unzip) {
        this.update = update;
        this.updateTrigger = updateTrigger;
        this.unzip = unzip;
    }

    @Override
    public void configure() throws Exception {
        from(updateTrigger).log(LoggingLevel.INFO, "Periodic updateAll triggered.").to(UPDATE_PLUG);
        from(UPDATE_PLUG)
                .process(unzip)
                .process(update)
                .wireTap( PeristenceRoutes.UPDATE_ALL )
                .setBody( e -> e.getIn().getHeader(ImageFromFolder.UPDATE_RESULT_HEADER ) );

    }

}
