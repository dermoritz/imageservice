package de.ml.persitence;

import java.time.ZonedDateTime;

import com.google.common.util.concurrent.AtomicLongMap;

import lombok.Data;
import lombok.NonNull;

@Data
public class ImageDocument {

    @NonNull
    private ImageId _id;

    /**
     * Last time the image was fetched through an end point
     */
    private ZonedDateTime lastFetched;

    /**
     *
     */
    private AtomicLongMap<Endpoints> countByEndpoint;

    public ImageDocument() {
        countByEndpoint = AtomicLongMap.create();
    }
}
