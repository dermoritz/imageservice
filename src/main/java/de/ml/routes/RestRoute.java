package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.endpoints.RestEndpointProvider.RandomImageEndpoint;
import de.ml.processors.SendFile.SendFileProc;

public class RestRoute extends RouteBuilder {

    private Processor sendFile;
    private Endpoint randImageEndpoint;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile, @RandomImageEndpoint Endpoint randImageEndpoint){
        this.sendFile = sendFile;
        this.randImageEndpoint = randImageEndpoint;

    }

    @Override
    public void configure() throws Exception {
        restConfiguration().component("restlet").apiContextPath("/").port(8082);
        from(randImageEndpoint).process(sendFile);
    }

}
