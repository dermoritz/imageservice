package de.ml.persitence;

import lombok.Data;
import lombok.NonNull;

/**
 * Identifies an image
 */
@Data
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
}
