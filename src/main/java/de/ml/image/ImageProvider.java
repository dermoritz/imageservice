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
     * @return random image with given string in path or name or null
     */
    File getWithName(String inName);

    /**
     *
     * @param inName part of path or file name
     * @return count of files matching filter
     */
    int getCountWithName(String inName);
}
