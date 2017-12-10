package de.ml.zip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by moritz on 27.11.2016.
 */
public class UnzipAll implements Runnable {

    private static final PathMatcher ZIP_FILE_PATTERN = FileSystems.getDefault()
            .getPathMatcher("glob:*.{zip}");

    private final Path rootFolder;

    private Logger LOG = LoggerFactory.getLogger(UnzipAll.class);
    private List<Path> zips;
    private List<Path> unzipped = new ArrayList<>();

    public UnzipAll(Path rootFolder) {
        if (!Files.isReadable(rootFolder) || !Files.isDirectory(rootFolder)) {
            throw new IllegalArgumentException("Given path is either no directory or isn't readable. Given: " + rootFolder);
        }
        this.rootFolder = rootFolder;
        searchZips();
    }

    public int getZipCount() {
        return zips.size();
    }

    private void searchZips() {
        try {
            zips = Files.find(rootFolder, Integer.MAX_VALUE, (path, attr) -> attr.isRegularFile() && ZIP_FILE_PATTERN.matches(path.getFileName())).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Problem on scanning folder for zips. Folder to be scanned: " + rootFolder);
        }
    }

    private void unzip(Path zipFile) {
        LOG.info("Unzipping " + zipFile);
        try {
            ZipUtil.explode(zipFile.toFile());
            LOG.info("Finished unzipping and deleted " + zipFile);
        } catch (Exception e) {
            LOG.error("Problem unzipping file: " + zipFile + " cause: ", e);
        }
        unzipped.add(zipFile);
    }

    @Override
    public void run() {
        zips.stream().forEach(this::unzip);

    }


}
