package de.ml.persitence;

import java.time.ZonedDateTime;
import java.util.EnumMap;

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
    private EnumMap<Endpoints, Integer> countByEndpoint;

}
