package de.ml.processors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;

import javax.inject.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mongodb.MongoDbConstants;

import de.ml.persitence.ImageDocument;
import de.ml.processors.UpdateImageDB.UpdateImageDBProcessor;

@UpdateImageDBProcessor
public class UpdateImageDB implements Processor {
    @Override
    public void process( Exchange exchange ) {
        Path file = exchange.getIn().getBody( Path.class );
        if( file == null ) {
            throw new IllegalArgumentException( "Expecting a body of type path but got " + exchange.getIn().getBody() );
        }
        ImageDocument imageDocument = new ImageDocument( file );
        exchange.getIn().setBody( new Object[]{ imageDocument.get_id().asWrappedDBObject(), imageDocument.updateImage() } );
        exchange.getIn().setHeader( MongoDbConstants.UPSERT, true );
    }

    @Qualifier
    @Retention( RetentionPolicy.RUNTIME )
    @Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE } )
    public @interface UpdateImageDBProcessor {

    }
}
