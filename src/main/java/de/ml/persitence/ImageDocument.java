package de.ml.persitence;

import lombok.Data;

@Data
public class ImageDocument {
    /**
     * An identifying part of the file path.
     */
    private String fileName;
    /**
     * Files size.
     */
    private Long size;

}
