package de.ml.image;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ThreadPoolBuilder;
import org.slf4j.Logger;

import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import de.ml.boot.ArgsConfiguration.Folder;
import de.ml.image.ImageFromFolder.ImageProviderImpl;

@Singleton
@ImageProviderImpl
public class ImageFromFolder implements ImageProvider, Processor {

    private File folder;

    private volatile List<Path> files = Lists.newArrayList();

    private Logger log;

    private Random random = new Random();

    private ExecutorService exec;

    private Future<Void> currentTask;

    private Cache<String, List<Path>> cache;

    @Inject
    private ImageFromFolder(@Folder File folder, Logger log, CamelContext context) {
        this.folder = folder;
        this.log = log;
        try {
            exec = new ThreadPoolBuilder(context).poolSize(1).maxPoolSize(1).build("fetch files");
        } catch (Exception e) {
            throw new IllegalStateException("Problem on creating executor: ", e);
        }
        setupCache();
        fetchAllFiles();
    }

    private void setupCache() {
        cache = CacheBuilder.newBuilder().build();
    }

    @Override
    public File getRandom() {
        return getRandomOf(files).toFile();
    }

    private Path getRandomOf(List<Path> list) {
        if (list.size() > 0) {
            int index = random.nextInt(list.size());
            Path file = list.get(index);
            if (file.toFile().canRead()) {
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
        if (currentTask == null || currentTask.isDone() || currentTask.isCancelled()) {
            currentTask = exec.submit(new FetchFilesTask());
        } else {
            log.info("Reject to update files, previous update not finished yet.");
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        int oldFileCount = files.size();
        fetchAllFiles();
        //wait for result
        currentTask.get();
        String message = "Update yielded " + (files.size()-oldFileCount) + " files.";
        log.info(message);
        exchange.getIn().setBody(message);
    }

    private class FetchFilesTask implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            log.info("Starting file update...");
            Stopwatch stopwatch = Stopwatch.createStarted();
            cache.invalidateAll();
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
            log.info("... " + files.size() + " files found. Walking the folder tree took "
                     + stopwatch.elapsed(TimeUnit.SECONDS)
                     + "s.");
            return null;
        }

    }

    private List<Path> getFilesWithNameContains(String inName) {
        log.info("fetching files with " + inName + " in path...");
        ArrayList<Path> result = Lists.newArrayList();
        for (Path path : files) {
            if (path.toString().toLowerCase().contains(inName.toLowerCase())) {
                result.add(path);
            }
        }
        return result;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface ImageProviderImpl {

    }

    @Override
    public File getWithName(String inName) {
        try {
            Path file = getRandomOf(cache.get(inName, () -> getFilesWithNameContains(inName)));
            return file == null ? null : file.toFile();
        } catch (ExecutionException e) {
            throw new IllegalStateException("Problem on loading list from cache: ", e);
        }
    }
}
