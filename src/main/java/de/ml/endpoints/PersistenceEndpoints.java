package de.ml.endpoints;

import org.apache.camel.Endpoint;

public interface PersistenceEndpoints {
    /**
     * Endpoint used to update an image path
     * @return
     */
    Endpoint updateImage();

    /**
     * Endpoint to retrieve an image document by id.
     * @return
     */
    Endpoint readById();

    /**
     * Endpoint to remove an image document from db.
     * @return
     */
    Endpoint removeById();

}
