package de.ml.persitence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mongodb.BasicDBObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Identifies an image
 */
@Data
@NoArgsConstructor
public class ImageId {
    /**
     * Filename or relevant part of path (should be system independent)
     */
    @NonNull
    private String fileName;

    /**
     * Size of file in bytes.
     */
    @NonNull
    private Long size;

    public ImageId( Path file ) {
        if(!Files.isRegularFile( file ) || !Files.isReadable( file )){
            throw new IllegalArgumentException( "Given file isn't readable or is't a regular file. File given " + file.toString());
        }
        this.fileName = file.getFileName().toString();
        try {
            this.size = Files.size(file);
        } catch( IOException e ) {
            throw new IllegalStateException( "Problem reading file size of file " + file.toString() +", cause: ", e );
        }
    }

    /**
     * An {@link BasicDBObject} {"_id":{"filename":"name","size":"size"}}
     * @return
     */
    public BasicDBObject asDBObject(){
        BasicDBObject result = new BasicDBObject();
        BasicDBObject id = new BasicDBObject();
        id.append( "fileName", fileName ).append( "size", size );

        return result.append( "_id", id );
    }
}
