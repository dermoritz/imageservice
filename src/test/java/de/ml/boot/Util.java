package de.ml.boot;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.google.common.io.Files;

public class Util {
    public static String getFolderOfResourceFolder(final String resourceFolder) {
        String folder;
        try {
            if (Util.isWindows()) {
                folder = URLDecoder.decode(ClassLoader.getSystemResource(resourceFolder).getPath()
                                                      .replaceFirst("/", ""),
                                           "UTF-8");
            } else {
                folder = URLDecoder.decode(ClassLoader.getSystemResource(resourceFolder).getPath(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
        return folder;
    }

    public static boolean isWindows() {
        return !System.getProperty("os.name").startsWith("Linux");
    }

    public static void copyAllFiles(final String fromFolder, final String toFolder) {
        File from = new File(fromFolder);
        if (!from.isDirectory()) {
            throw new IllegalArgumentException("fromFolder must be path to a directory. Given " + fromFolder);
        }
        File to = new File(toFolder);
        if (!to.isDirectory()) {
            throw new IllegalArgumentException("toFolder must be path to a directory. Given " + toFolder);
        }
        File[] listFiles = from.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            File toFile = new File(toFolder + File.separator + listFiles[i].getName());
            try {
                if (listFiles[i].isFile()) {
                    Files.copy(listFiles[i], toFile);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Problem on copying file: ", e);
            }
        }
    }
}
