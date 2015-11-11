package de.ml.processors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.google.common.base.Strings;

import de.ml.processors.SetAutoRefresh.SetAutoRefreshProc;
import de.ml.routes.RestRoute;

@SetAutoRefreshProc
public class SetAutoRefresh implements Processor {
    private static final String DEFAULT_AUTO_TIME = "5";

    @Override
    public void process(Exchange exchange) throws Exception {
        String uri = exchange.getIn().getHeader(RestRoute.HTTP_URI_HEADER, String.class);
        String time = exchange.getIn().getHeader(RestRoute.HEADER_AUTO_PARAMETER, String.class);
        if (Strings.isNullOrEmpty(time)) {
            time = DEFAULT_AUTO_TIME;
        }
        exchange.getIn().setHeader("Refresh", time + ";url="+uri);
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface SetAutoRefreshProc {

    }

}
