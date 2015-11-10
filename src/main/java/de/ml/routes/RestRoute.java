package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.ml.boot.ArgsConfiguration.Port;
import de.ml.processors.SendFile.SendFileProc;
import de.ml.processors.SetAutoRefresh.SetAutoRefreshProc;

public class RestRoute extends RouteBuilder {
    public static final String HEADER_NAME_PARAMETER = "inName";
    private static final String DIRECT_NEXT = "direct:next";
    private static final String DIRECT_NEXT_AUTO = "direct:auto";
    public static final String HEADER_AUTO_PARAMETER = "autoTime";
    public static final String AUTO_PATH = "auto";
    private Processor sendFile;
    private Integer port;
    private Processor setAutoHeader;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile, @Port Integer port,
                      @SetAutoRefreshProc Processor setAutoHeader) {
        this.sendFile = sendFile;
        this.port = port;
        this.setAutoHeader = setAutoHeader;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration().component("restlet").port(port);
        rest()
              .get("/next").to(DIRECT_NEXT)
              .get("/next/"+AUTO_PATH).to(DIRECT_NEXT_AUTO)
              .get("/next/"+AUTO_PATH+ "/{" + HEADER_AUTO_PARAMETER + "}").to(DIRECT_NEXT_AUTO)
              .get("/{" + HEADER_NAME_PARAMETER + "}").to(DIRECT_NEXT)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH).to(DIRECT_NEXT_AUTO)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}")
              .to(DIRECT_NEXT_AUTO);
        from(DIRECT_NEXT).process(sendFile);
        from(DIRECT_NEXT_AUTO).process(setAutoHeader).to(DIRECT_NEXT);
    }

};