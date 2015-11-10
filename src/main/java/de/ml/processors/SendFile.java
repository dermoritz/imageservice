package de.ml.processors;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;

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

@SendFileProc
public class SendFile implements Processor {

    private ImageProvider ip;

    @Inject
    private SendFile(@ImageProviderImpl ImageProvider ip) {
        this.ip = ip;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String inName = exchange.getIn().getHeader(RestRoute.HEADER_NAME_PARAMETER, String.class);
        File random;
        if (Strings.isNullOrEmpty(inName)) {
            random = ip.getRandom();
        }else{
            random = ip.getWithName(inName);
        }
        if (random != null) {
            String mediaType = Files.probeContentType(random.toPath());
            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, mediaType);
            exchange.getIn().setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            exchange.getIn().setHeader(HttpHeaders.CONTENT_DISPOSITION,
                                       "inline; filename=\"" + random.getName() + "\"");
            exchange.getIn().setHeader(HttpHeaders.PRAGMA, "no-cache");
            exchange.getIn().setHeader(HttpHeaders.EXPIRES, "0");
            exchange.getIn().setBody(random);

        } else {
            exchange.getIn().setBody("Got no image :-(, maybe try later...");
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface SendFileProc {

    }
}
