package de.ml.persitence;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Set;

import com.mongodb.BasicDBObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Object representing data do be persisted for an image. equals and hashCode only rely on {@link ImageDocument#_id}.
 */
@Data
@NoArgsConstructor
public class ImageDocument {

    @NonNull
    private ImageId _id;

    /**
     * Last time (should be iso string) the image was fetched through an end point
     */
    private ZonedDateTime lastFetched;

    /**
     * Last time this image was found during an update. Old dates (older than last update run) indicate deletion of image.
     */
    private ZonedDateTime lastUpdated;

    /**
     * Current file path
     */
    private String currentPath;

    /**
     * Image tags
     */
    private Set<String> tags;

    /**
     *
     */
    private EnumMap<Endpoints, Integer> countByEndpoint = new EnumMap<>( Endpoints.class );

    public ImageDocument( Path file ) {
        _id = new ImageId( file );
        currentPath = file.toString();
    }

    /**
     * {@link BasicDBObject} that contains only fields to be updated on update operation.
     *
     * @return Object to be used as 2nd argument in update operation
     */
    public BasicDBObject updateImage() {
        lastUpdated = ZonedDateTime.now();
        BasicDBObject result = new BasicDBObject();
        result.append( "lastUpdated", lastUpdated.format( DateTimeFormatter.ISO_OFFSET_DATE_TIME ) )
                .append( "currentPath", currentPath );
        return new BasicDBObject().append( "$set", result );
    }

    public BasicDBObject countAccess( Endpoints endpoint ) {
        lastFetched = ZonedDateTime.now();
        return new BasicDBObject().append( "$inc", new BasicDBObject().append( "countByEndpoint." + endpoint.name(), 1 ) )
                .append( "$set",
                        new BasicDBObject().append( "lastFetched", lastFetched.format( DateTimeFormatter.ISO_OFFSET_DATE_TIME ) ) );
    }

    public BasicDBObject addTags(Set<String> tags){
        return new BasicDBObject( "$addToSet", new BasicDBObject( "tags", new BasicDBObject( "$each", tags ) ) );
    }

    public BasicDBObject removeTags(Set<String> tags){
        return new BasicDBObject( "$pull", new BasicDBObject( "tags",new BasicDBObject( "$in", tags ) ) );
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof ImageDocument ) ) {
            return false;
        }
        ImageDocument that = (ImageDocument) o;
        return Objects.equals( get_id(), that.get_id() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( get_id() );
    }
}
