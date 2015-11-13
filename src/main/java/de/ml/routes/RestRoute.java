package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.HttpStatus;

import de.ml.boot.ArgsConfiguration.Port;
import de.ml.image.ImageFromFolder.ImageProviderImpl;
import de.ml.processors.SendFile.SendFileProc;
import de.ml.processors.SetAutoRefresh.SetAutoRefreshProc;

public class RestRoute extends RouteBuilder {
    public static final String HISTORY_HEADER = "history";
    private static final String DIRECT_INFO = "direct:info";
    private static final String DIRECT_PREV = "direct:prev";
    private static final String DIRECT_UPDATE = "direct:update";
    public static final String HEADER_NAME_PARAMETER = "inName";
    private static final String DIRECT_NEXT = "direct:next";
    private static final String DIRECT_NEXT_AUTO = "direct:auto";
    public static final String HEADER_AUTO_PARAMETER = "autoTime";
    public static final String AUTO_PATH = "auto";
    public static final String HTTP_URI_HEADER = "CamelHttpUri";
    private Processor sendFile;
    private Integer port;
    private Processor setAutoHeader;
    private Processor imageProvider;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile, @Port Integer port,
                      @SetAutoRefreshProc Processor setAutoHeader, @ImageProviderImpl Processor imageProvider) {
        this.sendFile = sendFile;
        this.port = port;
        this.setAutoHeader = setAutoHeader;
        this.imageProvider = imageProvider;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration().component("restlet").port(port);
        intercept().when(header(HTTP_URI_HEADER).endsWith("favicon.ico")).setHeader(Exchange.HTTP_RESPONSE_CODE)
                   .constant(HttpStatus.SC_NOT_FOUND).stop();
        rest()
              .get("/next").to(DIRECT_NEXT)
              .get("/next/" + AUTO_PATH).to(DIRECT_NEXT_AUTO)
              .get("/next/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}").to(DIRECT_NEXT_AUTO)
              .get("/update").to(DIRECT_UPDATE)
              .get("/prev").to(DIRECT_PREV)
              .get("/info").to(DIRECT_INFO)
              .get("/{" + HEADER_NAME_PARAMETER + "}").to(DIRECT_NEXT)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH).to(DIRECT_NEXT_AUTO)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}")
              .to(DIRECT_NEXT_AUTO);
        from(DIRECT_NEXT).process(sendFile);
        from(DIRECT_NEXT_AUTO).process(setAutoHeader).to(DIRECT_NEXT);
        from(DIRECT_UPDATE).process(imageProvider);
        from(DIRECT_PREV).setHeader(HISTORY_HEADER,constant(History.PREV)).process(sendFile);
        from(DIRECT_INFO).setHeader(HISTORY_HEADER, constant(History.INFO)).process(sendFile);
    }

    public enum History{
        PREV,
        INFO;
    }

}