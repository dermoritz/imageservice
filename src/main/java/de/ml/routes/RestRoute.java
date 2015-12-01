package de.ml.routes;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.HttpStatus;

import de.ml.endpoints.RestEndpoints;
import de.ml.image.ImageFromFolder.ImageProviderImpl;
import de.ml.processors.SendFile.SendFileProc;
import de.ml.processors.SetAutoRefresh.SetAutoRefreshProc;

public class RestRoute extends RouteBuilder {
    public static final String HISTORY_HEADER = "history";

    private static final String DIRECT_NEXT = "direct:next";
    private static final String DIRECT_NEXT_AUTO = "direct:auto";


    public static final String HTTP_URI_HEADER = "CamelHttpUri";
    private Processor sendFile;
    private Processor setAutoHeader;
    private Processor imageProvider;
    private Endpoint next;
    private Endpoint nextAuto;
    private Endpoint nextAutoTime;
    private Endpoint update;
    private Endpoint prev;
    private Endpoint info;
    private Endpoint current;
    private Endpoint filterName;
    private Endpoint filterNameAuto;
    private Endpoint filterNameInfo;
    private Endpoint filterNameAutoTime;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile,
                      @SetAutoRefreshProc Processor setAutoHeader, @ImageProviderImpl Processor imageProvider,
                      RestEndpoints restEndpoints) {
        this.sendFile = sendFile;
        this.setAutoHeader = setAutoHeader;
        this.imageProvider = imageProvider;
        this.next = restEndpoints.next();
        this.nextAuto = restEndpoints.nextAuto();
        this.nextAutoTime = restEndpoints.nextAutoTime();
        this.update = restEndpoints.update();
        this.prev = restEndpoints.prev();
        this.info = restEndpoints.info();
        this.current = restEndpoints.current();
        this.filterName = restEndpoints.filterName();
        this.filterNameAuto = restEndpoints.filterNameAuto();
        this.filterNameInfo = restEndpoints.filterNameInfo();
        this.filterNameAutoTime = restEndpoints.filterNameAutoTime();
    }

    @Override
    public void configure() throws Exception {
        intercept().when(header(HTTP_URI_HEADER).endsWith("favicon.ico")).setHeader(Exchange.HTTP_RESPONSE_CODE)
                   .constant(HttpStatus.SC_NOT_FOUND).stop();
        from(DIRECT_NEXT_AUTO).process(setAutoHeader).to(DIRECT_NEXT);
        from(next).to(DIRECT_NEXT);
        from(nextAuto).to(DIRECT_NEXT_AUTO);
        from(nextAutoTime).to(DIRECT_NEXT_AUTO);
        from(update).process(imageProvider);
        from(prev).setHeader(HISTORY_HEADER, constant(History.PREV)).process(sendFile);
        from(info).setHeader(HISTORY_HEADER, constant(History.INFO)).process(sendFile);
        from(current).setHeader(HISTORY_HEADER, constant(History.CURRENT)).process(sendFile);
        from(filterName).to(DIRECT_NEXT);
        from(filterNameAuto).to(DIRECT_NEXT_AUTO);
        from(filterNameInfo).setHeader(HISTORY_HEADER, constant(History.FILTER_INFO)).process(sendFile);
        from(filterNameAutoTime).to(DIRECT_NEXT_AUTO);
        from(DIRECT_NEXT).process(sendFile);
    }

    public enum History {
        PREV, INFO, FILTER_INFO, CURRENT;
    }

}