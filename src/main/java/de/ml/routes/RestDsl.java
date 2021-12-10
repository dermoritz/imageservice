package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.boot.ArgsConfiguration.Port;
import de.ml.processors.SendFile;

/**
 * Created by moritz on 01.01.2017.
 */
public class RestDsl extends RouteBuilder {

    private final Integer port;
    private final Processor sendFile;
    private static final String DIRECT_NEXT = "direct:next-swagger";

    @Inject
    public RestDsl(@Port Integer port, @SendFile.SendFileProc Processor sendFile) {
        this.port = port;
        this.sendFile = sendFile;
    }


    @Override
    public void configure() throws Exception {
//        restConfiguration().component("restlet")
//                .host("localhost")
//                .contextPath("/swaggered")
//                .port(port)
//                .apiContextPath("/api-doc")
//                .apiProperty("api.title", "Imageservice REST Api");
//
//        rest("/image")
//                .get("/next").description("Returns random image.").to(DIRECT_NEXT);
//
//        from(DIRECT_NEXT).process(sendFile);
    }
}
