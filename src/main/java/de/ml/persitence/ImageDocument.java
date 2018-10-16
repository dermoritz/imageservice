package de.ml.persitence;

import com.google.common.util.concurrent.AtomicLongMap;
import lombok.Data;

import java.time.LocalDateTime;

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

    /**
     *
     */
    private LocalDateTime lastFetched;

    /**
     *
     */
    private AtomicLongMap<String> countByEndpoint;
}
