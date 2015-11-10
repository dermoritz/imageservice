package de.ml.image;

import java.io.File;

public interface ImageProvider {
    /**
     *
     * @return random image or null if no image exists.
     */
    File getRandom();

    /**
     * Returns file with given string somewhere in path or file name.
     * @param inName part of path or file name
     * @return random image with given string in path or name
     */
    File getWithName(String inName);
}
