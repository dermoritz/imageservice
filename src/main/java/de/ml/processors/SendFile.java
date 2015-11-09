package de.ml.processors;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import de.ml.image.ImageProvider;
import de.ml.processors.SendFile.SendFileProc;

@SendFileProc
public class SendFile implements Processor {


    private ImageProvider ip;

    @Inject
    private SendFile(ImageProvider ip) {
        this.ip = ip;
    }


    @Override
    public void process(Exchange exchange) throws Exception {
        File random = ip.getRandom();
        if(random!=null){
            exchange.getIn().setBody(random);
        } else{
            exchange.getIn().setBody("Got no image :-(, maybe try later...");
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface SendFileProc {

    }
}
