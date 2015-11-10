package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.boot.ArgsConfiguration.Port;
import de.ml.processors.SendFile.SendFileProc;

public class RestRoute extends RouteBuilder {
    public static final String HEADER_PARAMETER = "inName";
    private static final String DIRECT_NEXT = "direct:next";
    private Processor sendFile;
    private Integer port;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile, @Port Integer port) {
        this.sendFile = sendFile;
        this.port = port;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration().component("restlet").port(port);
        rest("/next")
                     .get().to(DIRECT_NEXT)
                     .get("/{"+HEADER_PARAMETER+"}").to(DIRECT_NEXT);
        from(DIRECT_NEXT).process(sendFile);
    }

}
