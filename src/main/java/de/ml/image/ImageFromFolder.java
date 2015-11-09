package de.ml.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import de.ml.boot.ArgsConfiguration.Folder;

@Singleton
public class ImageFromFolder implements ImageProvider, Processor {

    private File folder;

    private volatile List<Path> files = Lists.newArrayList();

    private Logger log;

    private Random random = new Random();

    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Inject
    private ImageFromFolder(@Folder File folder, Logger log) {
        this.folder = folder;
        this.log = log;
        fetchAllFiles();
    }

    @Override
    public File getRandom() {
        if (files.size() > 0) {
            int index = random.nextInt(files.size());
            File file = files.get(index).toFile();
            if (file.canRead()) {
                return file;
            } else {
                log.info("File " + file + " not found updating list...");
                fetchAllFiles();
                return null;
            }
        } else {
            fetchAllFiles();
            return null;
        }
    }

    private void fetchAllFiles() {
        if (((ThreadPoolExecutor) exec).getActiveCount() < 1) {
            exec.submit(() -> {
                Stopwatch stopwatch = Stopwatch.createStarted();
                files.clear();
                try {
                    Files.walkFileTree(folder.toPath(), new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (!attrs.isDirectory() && file.getFileName().toString().endsWith("jpg")) {
                                files.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                    });
                } catch (IOException e) {
                    throw new IllegalArgumentException("Problem reading folder: ", e);
                }
                stopwatch.stop();
                log.info(files.size() + " files found. Walking the folder tree took "
                         + stopwatch.elapsed(TimeUnit.SECONDS)
                         + "s.");
            });
        } else {
            log.info("Rejected update of files, old task still running");
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        fetchAllFiles();
    }
}
