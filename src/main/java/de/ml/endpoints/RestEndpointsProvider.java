package de.ml.endpoints;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.component.restlet.RestletEndpoint;
import org.restlet.data.Method;

import com.google.common.base.Preconditions;

import de.ml.boot.AllowedUserProvider.AllowedUsers;
import de.ml.boot.ArgsConfiguration.Port;
import de.ml.statistic.StatisticImpl;

public class RestEndpointsProvider implements RestEndpoints {

    private static final String PREV_PATH = "/prev";
    public static final String HEADER_PREV_OFFSET_PARAMETER = "prevOffset";
    public static final String NEXT = "next";
    private Map<String, String> users;
    private CamelContext context;
    private Integer port;
    public static final String HEADER_AUTO_PARAMETER = "autoTime";
    public static final String HEADER_NAME_PARAMETER = "inName";
    public static final String AUTO_PATH = "auto";
    public static final String INFO_PATH = "info";
    public static final String SORT_PATH = "sort";
    public static final String INDEX_PATH = "index";
    public static final String INDEX_FILTER_PATH = "indexFilter";
    public static final String INDEX_INFO_PATH = "indexInfo";
    private static final String INDEX_FILTER_INFO = "indexFilterInfo";
    public static final String HEADER_INDEX_PARAMETER = "index";

    @Inject
    private RestEndpointsProvider(CamelContext context, @AllowedUsers Map<String, String> users, @Port Integer port) {
        this.context = Preconditions.checkNotNull(context);
        this.users = Preconditions.checkNotNull(users);
        this.port = Preconditions.checkNotNull(port);
        // http://camel.apache.org/restlet.html says this yields better
        // performance due to this:
        // https://github.com/restlet/restlet-framework-java/issues/996
        context.getComponent("restlet", RestletComponent.class).setSynchronous(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#next()
     */
    @Override
    public Endpoint next() {
        return getRestEndpoint("/" + NEXT);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#nextAuto()
     */
    @Override
    public Endpoint nextAuto() {
        return getRestEndpoint("/" + NEXT + "/" + AUTO_PATH);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#nextAutoTime()
     */
    @Override
    public Endpoint nextAutoTime() {
        return getRestEndpoint("/" + NEXT + "/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#updateAll()
     */
    @Override
    public Endpoint update() {
        return getRestEndpoint("/updateAll");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#prev()
     */
    @Override
    public Endpoint prev() {
        return getRestEndpoint(PREV_PATH);
    }

    @Override
    public Endpoint prevOffset() {
        return getRestEndpoint(PREV_PATH + "/{" + HEADER_PREV_OFFSET_PARAMETER + "}");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#info()
     */
    @Override
    public Endpoint info() {
        return getRestEndpoint("/" + INFO_PATH);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#current()
     */
    @Override
    public Endpoint current() {
        return getRestEndpoint("/current");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#filterName()
     */
    @Override
    public Endpoint filterName() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#filterNameAuto()
     */
    @Override
    public Endpoint filterNameAuto() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#filterNameInfo()
     */
    @Override
    public Endpoint filterNameInfo() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + INFO_PATH);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.ml.endpoints.RestEndpoints#filterNameAutoTime()
     */
    @Override
    public Endpoint filterNameAutoTime() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}");
    }

    @Override
    public Endpoint filterNameSort() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + SORT_PATH);
    }

    @Override
    public Endpoint filterNameAutoSort() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + SORT_PATH + "/" + AUTO_PATH);
    }

    @Override
    public Endpoint filterNameAutoTimeSort() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + SORT_PATH + "/" + AUTO_PATH + "/{"
                               + HEADER_AUTO_PARAMETER + "}");
    }

    @Override
    public Endpoint byIndex() {
        return getRestEndpoint("/" + INDEX_PATH + "/{" + HEADER_INDEX_PARAMETER + "}");
    }

    @Override
    public Endpoint maxIndex() {
        return getRestEndpoint("/" + INDEX_PATH );
    }

    @Override
    public Endpoint byIndexFiltered() {
        return getRestEndpoint("/" + INDEX_FILTER_PATH + "/{" + HEADER_NAME_PARAMETER + "}/{" + HEADER_INDEX_PARAMETER + "}");
    }

    @Override
    public Endpoint byIndexInfo() {
        return getRestEndpoint("/" + INDEX_INFO_PATH + "/{" + HEADER_INDEX_PARAMETER + "}");

    }

    @Override
    public Endpoint byIndexFilteredInfo() {
        return getRestEndpoint("/" + INDEX_FILTER_INFO + "/{" + HEADER_NAME_PARAMETER + "}/{" + HEADER_INDEX_PARAMETER + "}");
    }

    @Override
    public Endpoint statisticAvgDistance() {
        return getRestEndpoint("/statistic/" + StatisticImpl.AVG_DISTANCE_ENDPOINT);
    }

    @Override
    public Endpoint statisticDistChart() {
        return getRestEndpoint("/statistic/" + StatisticImpl.DISTRIBUTION_CHART);
    }

    private Endpoint getRestEndpoint(String path) {
        RestletEndpoint endpoint = context.getEndpoint("restlet:" + path, RestletEndpoint.class);
        endpoint.setRestletMethod(Method.GET);
        endpoint.setHost("0.0.0.0");
        endpoint.setProtocol("http");
        endpoint.setPort(port);
        endpoint.setRestletRealm(users);
        return endpoint;
    }

}
