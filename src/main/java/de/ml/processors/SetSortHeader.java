package de.ml.processors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import de.ml.processors.SetSortHeader.SetSortProc;

@SetSortProc
public class SetSortHeader implements Processor {
    public static final String SORT_HEADER = "sortHeader";
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(SORT_HEADER, true);
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface SetSortProc {

    }
}
