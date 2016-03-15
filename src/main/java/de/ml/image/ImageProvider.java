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
     * Returns next image from a sorted list if called subsequently for same name.
     * Resets after call for new name. This will cycle around.
     * @param inName part of path or file name.
     * @return next image in order by name
     */
    File getWithNameSort(String inName);

    /**
     *
     * @param inName part of path or file name
     * @return count of files matching filter
     */
    int getCountWithName(String inName);

    /**
     *
     * @return max index to be retrieved. total count of images is maxIndex+1.
     */
    int maxIndex();

    /**
     *
     * @param index 0..maxIndex
     * @return image with given index
     */
    File byIndex(int index);

    /**
     *
     * @param filter filter string
     * @param index index of file
     * @return File with given index within filtered list. Max index is getCountWithName-1
     */
    File filterByIndex(String filter, int index);
}
