package de.ml.processors;

import de.ml.boot.ArgsConfiguration;
import de.ml.zip.UnzipAll;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.List;

/**
 * Created by moritz on 27.11.2016.
 */
@Named("unzip")
public class UnzipProcessor implements Processor {
    @Inject
    private Logger log;

    @Inject
    @ArgsConfiguration.Folder
    List<File> folders;

    @Override
    public void process(Exchange exchange) throws Exception {
        folders.parallelStream().forEach((this::unzip));
    }

    private void unzip(File file) {
        UnzipAll unzip = new UnzipAll(file.toPath());
        log.info("Unzipping folder " + file);
        unzip.run();
        log.info("Finished unzipping folder " + file);
    }
}
