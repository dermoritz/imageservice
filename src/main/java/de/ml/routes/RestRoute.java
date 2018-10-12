package de.ml.routes;

import java.io.IOException;

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
import de.ml.processors.SetSortHeader.SetSortProc;
import de.ml.statistic.Statistic;

public class RestRoute extends RouteBuilder {
    private static final String DIRECT_PREV = "direct:prev";

    private static final String DIRECT_SORT_AUTO = "direct:sortAuto";

    private static final String DIRECT_SORT = "direct:sort";

    public static final String MODE_HEADER = "history";

    private static final String DIRECT_NEXT = "direct:next";
    private static final String DIRECT_NEXT_AUTO = "direct:auto";

    public static final String HTTP_URI_HEADER = "CamelHttpUri";
    private final Endpoint avgDistance;
    private final Statistic statistic;
    private final Endpoint distChart;
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

    private Endpoint filterNameSort;

    private Endpoint filterNameAutoSort;

    private Endpoint filterNameAutoTimeSort;

    private Processor setSortHeader;

    private Endpoint prevOffset;

    private Endpoint byIndex;

    private Endpoint maxIndex;

    private Endpoint byIndexFiltered;

    private Endpoint byIndexInfo;

    private Endpoint byIndexFilteredInfo;

    @Inject
    private RestRoute(@SendFileProc Processor sendFile, @SetAutoRefreshProc Processor setAutoHeader,
                      @ImageProviderImpl Processor imageProvider, RestEndpoints restEndpoints,
                      @SetSortProc Processor setSortHeader, Statistic statistic) {
        this.sendFile = sendFile;
        this.setAutoHeader = setAutoHeader;
        this.imageProvider = imageProvider;
        this.setSortHeader = setSortHeader;
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
        byIndex = restEndpoints.byIndex();
        maxIndex = restEndpoints.maxIndex();
        byIndexFiltered = restEndpoints.byIndexFiltered();
        byIndexInfo = restEndpoints.byIndexInfo();
        byIndexFilteredInfo = restEndpoints.byIndexFilteredInfo();
        prevOffset = restEndpoints.prevOffset();
        filterNameSort = restEndpoints.filterNameSort();
        filterNameAutoSort = restEndpoints.filterNameAutoSort();
        filterNameAutoTimeSort = restEndpoints.filterNameAutoTimeSort();
        avgDistance = restEndpoints.statisticAvgDistance();
        distChart = restEndpoints.statisticDistChart();
		this.statistic = statistic;
    }

    @Override
    public void configure() throws Exception {
        // ioexception is thrown if client cancels loading or reloads before
        // finished.
        onException(IOException.class).handled(true);
        intercept().when(header(HTTP_URI_HEADER).endsWith("favicon.ico")).setHeader(Exchange.HTTP_RESPONSE_CODE)
                   .constant(HttpStatus.SC_NOT_FOUND).stop();
        from(DIRECT_NEXT_AUTO).process(setAutoHeader).to(DIRECT_NEXT);
        from(DIRECT_SORT).process(setSortHeader).to(DIRECT_NEXT);
        from(DIRECT_SORT_AUTO).process(setAutoHeader).process(setSortHeader).to(DIRECT_NEXT);
        // statistic
        from(avgDistance).process((Processor) statistic);
        from(distChart).process((Processor) statistic);
        // by index
        from(byIndex).setHeader(MODE_HEADER, constant(Mode.INDEX)).to(DIRECT_NEXT);
        from(maxIndex).setHeader(MODE_HEADER, constant(Mode.INDEX)).to(DIRECT_NEXT);
        from(byIndexFiltered).setHeader(MODE_HEADER, constant(Mode.INDEX_FILTERED)).to(DIRECT_NEXT);
        from(byIndexInfo).setHeader(MODE_HEADER, constant(Mode.INDEX_INFO)).to(DIRECT_NEXT);
        from(byIndexFilteredInfo).setHeader(MODE_HEADER, constant(Mode.INDEX_FILTER_INFO)).to(DIRECT_NEXT);
        // next
        from(next).to(DIRECT_NEXT);
        from(nextAuto).to(DIRECT_NEXT_AUTO);
        from(nextAutoTime).to(DIRECT_NEXT_AUTO);
        from(update).to(UpdateImages.UPDATE_PLUG);
        // previous
        from(DIRECT_PREV).setHeader(MODE_HEADER, constant(Mode.PREV)).process(sendFile);
        from(prev).to(DIRECT_PREV);
        from(prevOffset).to(DIRECT_PREV);
        // info
        from(info).setHeader(MODE_HEADER, constant(Mode.INFO)).process(sendFile);
        from(current).setHeader(MODE_HEADER, constant(Mode.CURRENT)).process(sendFile);
        // filter
        from(filterName).to(DIRECT_NEXT);
        from(filterNameSort).to(DIRECT_SORT);
        from(filterNameAuto).to(DIRECT_NEXT_AUTO);
        from(filterNameAutoSort).to(DIRECT_SORT_AUTO);
        from(filterNameInfo).setHeader(MODE_HEADER, constant(Mode.FILTER_INFO)).process(sendFile);
        from(filterNameAutoTime).to(DIRECT_NEXT_AUTO);
        from(filterNameAutoTimeSort).to(DIRECT_SORT_AUTO);
        //
        from(DIRECT_NEXT).process(sendFile).setHeader("Access-Control-Allow-Origin", constant("*"));
    }

    public enum Mode {
                      PREV, INFO, FILTER_INFO, CURRENT, INDEX, INDEX_FILTERED, INDEX_INFO, INDEX_FILTER_INFO
    }

}