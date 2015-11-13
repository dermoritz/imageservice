package de.ml.processors;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

import de.ml.image.ImageFromFolder.ImageProviderImpl;
import de.ml.image.ImageProvider;
import de.ml.processors.SendFile.SendFileProc;
import de.ml.routes.RestRoute;
import de.ml.routes.RestRoute.History;

@SendFileProc
public class SendFile implements Processor {

    private ImageProvider ip;
    private List<File> history = new ArrayList<>();
    private int currentIndex = 0;

    @Inject
    private SendFile(@ImageProviderImpl ImageProvider ip) {
        this.ip = ip;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        History historyHeader = exchange.getIn().getHeader(RestRoute.HISTORY_HEADER, History.class);
        if (historyHeader != null) {
            handleHistory(historyHeader, exchange);
        } else {
            String inName = URLDecoder.decode(Strings.nullToEmpty(exchange.getIn().getHeader(
                                                                                             RestRoute.HEADER_NAME_PARAMETER,
                                                                                             String.class)),
                                              StandardCharsets.UTF_8.name());
            File random;
            if (inName.isEmpty()) {
                random = ip.getRandom();
            } else {
                random = ip.getWithName(inName);
            }
            if (random != null) {
                setHeadersAndBody(exchange, random);
                history.add(random);
                currentIndex = history.size() - 1;
            } else {
                exchange.getIn().setBody("Got no image :-(, maybe try later.... Filter: " + inName);
            }
        }
    }

    private void handleHistory(History historyHeader, Exchange exchange) {
        switch (historyHeader) {
        case INFO:
            if (currentIndex >= 0) {
                exchange.getIn().setBody(history.get(currentIndex).getAbsolutePath());
            } else {
                exchange.getIn().setBody("nix");
            }
            break;
        case PREV:
            if (--currentIndex >= 0) {
                setHeadersAndBody(exchange, history.get(currentIndex));
            } else if (history.size() >= 0) {
                setHeadersAndBody(exchange, history.get(0));
            } else {
                exchange.getIn().setBody("nix");
            }
            break;
        case FILTER_INFO:
            String inName = exchange.getIn().getHeader(RestRoute.HEADER_NAME_PARAMETER, String.class);
            if (!Strings.isNullOrEmpty(inName)) {
                exchange.getIn().setBody(ip.getCountWithName(inName) + " files contain " + "\"" + inName + "\"");
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown header content: " + historyHeader);
        }

    }

    private void setHeadersAndBody(Exchange exchange, File file) {
        try {
            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            throw new IllegalStateException("Problem detecting media type: ", e);
        }
        exchange.getIn().setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        exchange.getIn().setHeader(HttpHeaders.CONTENT_DISPOSITION,
                                   "inline; filename=\"" + file.getName() + "\"");
        exchange.getIn().setHeader(HttpHeaders.PRAGMA, "no-cache");
        exchange.getIn().setHeader(HttpHeaders.EXPIRES, "0");
        exchange.getIn().setBody(file);
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface SendFileProc {

    }
}
