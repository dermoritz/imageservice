package de.ml.image;

import java.io.File;

public interface ImageProvider {
    /**
     *
     * @return random image or null if no image exists.
     */
    File getRandom();
}
