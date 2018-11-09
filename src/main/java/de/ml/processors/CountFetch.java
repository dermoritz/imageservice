package de.ml.processors;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mongodb.MongoDbConstants;

import com.google.common.base.Strings;

import de.ml.persitence.Endpoints;
import de.ml.persitence.ImageDocument;
import de.ml.processors.CountFetch.CountFetchProcessor;
import de.ml.routes.RestRoute;
import de.ml.routes.RestRoute.Mode;

@CountFetchProcessor
public class CountFetch implements Processor {
    @Override
    public void process( Exchange exchange ) {
        File file = exchange.getIn().getBody( File.class );
        //only count fetch if file is sent
        if( file != null ) {
            Endpoints e = mapToEndpoint( exchange );
            ImageDocument imageDocument = new ImageDocument( file.toPath() );
            exchange.getIn().setBody( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( e )} );
            exchange.getIn().setHeader( MongoDbConstants.UPSERT, false );
        }
    }

    private Endpoints mapToEndpoint( Exchange exchange ) {
        Mode historyHeader = exchange.getIn().getHeader( RestRoute.MODE_HEADER, Mode.class );
        if(historyHeader == null && !Strings.isNullOrEmpty( SendFile.getNameParameter( exchange ) )) {
            return Endpoints.BYFILTER;
        } else if(  historyHeader == null || historyHeader == Mode.PREV || historyHeader == Mode.CURRENT ) {
            return Endpoints.RANDOM;
        } else {
            return Endpoints.BYINDEX;
        }
    }

    @Qualifier
    @Retention( RetentionPolicy.RUNTIME )
    @Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE } )
    public @interface CountFetchProcessor {

    }
}
