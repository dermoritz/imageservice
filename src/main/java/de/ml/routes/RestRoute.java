package de.ml.routes;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.HttpStatus;

import de.ml.boot.ArgsConfiguration.Port;
import de.ml.boot.Boot;
import de.ml.image.ImageFromFolder.ImageProviderImpl;
import de.ml.processors.SendFile.SendFileProc;
import de.ml.processors.SetAutoRefresh.SetAutoRefreshProc;

public class RestRoute extends RouteBuilder {
    private static final String DIRECT_CURRENT = "direct:current";
    public static final String RESTLET_REALM_KEY = "restletRealmRef";
    private static final String DIRECT_FILTER_INFO = "direct:filter_info";
    private static final String FILTER_INFO_PATH = "info";
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

        restConfiguration().component("restlet").port(port)
        //.endpointProperty(RESTLET_REALM_KEY, Boot.REALM_REG_KEY)
        ;

        intercept().when(header(HTTP_URI_HEADER).endsWith("favicon.ico")).setHeader(Exchange.HTTP_RESPONSE_CODE)
                   .constant(HttpStatus.SC_NOT_FOUND).stop();
        //ioexception occurs if reload is pressed before image was fully delivered - so it could be swallowed
        onException(IOException.class).handled(true).stop();
        rest()
              .get("/next").to(DIRECT_NEXT)
              .get("/next/" + AUTO_PATH).to(DIRECT_NEXT_AUTO)
              .get("/next/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}").to(DIRECT_NEXT_AUTO)
              .get("/update").to(DIRECT_UPDATE)
              .get("/prev").to(DIRECT_PREV)
              .get("/info").to(DIRECT_INFO)
              .get("/current").to(DIRECT_CURRENT)
              .get("/{" + HEADER_NAME_PARAMETER + "}").to(DIRECT_NEXT)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH).to(DIRECT_NEXT_AUTO)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + FILTER_INFO_PATH).to(DIRECT_FILTER_INFO)
              .get("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}")
              .to(DIRECT_NEXT_AUTO);
        from(DIRECT_NEXT).process(sendFile);
        from(DIRECT_NEXT_AUTO).process(setAutoHeader).to(DIRECT_NEXT);
        from(DIRECT_UPDATE).process(imageProvider);
        from(DIRECT_PREV).setHeader(HISTORY_HEADER,constant(History.PREV)).process(sendFile);
        from(DIRECT_INFO).setHeader(HISTORY_HEADER, constant(History.INFO)).process(sendFile);
        from(DIRECT_FILTER_INFO).setHeader(HISTORY_HEADER, constant(History.FILTER_INFO)).process(sendFile);
        from(DIRECT_CURRENT).setHeader(HISTORY_HEADER, constant(History.CURRENT)).process(sendFile);
    }

    public enum History{
        PREV,
        INFO,
        FILTER_INFO,
        CURRENT;
    }

}