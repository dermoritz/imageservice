package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.boot.ArgsConfiguration.Port;
import de.ml.endpoints.RestEndpointProvider.RandomImageEndpoint;
import de.ml.processors.SendFile.SendFileProc;

public class RestRoute extends RouteBuilder {

    private Processor sendFile;
    private Endpoint randImageEndpoint;
    private Integer port;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile, @RandomImageEndpoint Endpoint randImageEndpoint,
                      @Port Integer port) {
        this.sendFile = sendFile;
        this.randImageEndpoint = randImageEndpoint;
        this.port = port;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration().component("restlet").apiContextPath("/").port(port);
        from(randImageEndpoint).process(sendFile);
    }

}
