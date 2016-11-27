package de.ml.boot;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.bindings.Parameters;
import org.slf4j.Logger;

import com.google.common.collect.Lists;

@Singleton
public class ArgsConfiguration {

    private static final int DEFAULT_PORT = 80;
    private List<File> folders;
    private List<String> args;
    private static final String FOLDER_DELIMITER = ";";
    @Inject
    private Logger log;

    @Inject
    private ArgsConfiguration(@Parameters List<String> args) {
        this.args = args;
        checkArgument(args.size() > 0, "Missing argument to specify folder.");
        folders = checkFolder(Arrays.asList(args.get(0).split(FOLDER_DELIMITER)));

    }

    @Produces
    @Folder
    public List<File> getFolder() {
        return folders;
    }

    @Produces
    @Port
    public Integer getPort(){
        int port = DEFAULT_PORT;
        if(args.size()>1){
            try{
                port = Integer.parseInt(args.get(1));
            }catch(NumberFormatException e){
                log.warn("Second argument (Port) not parsable to integer, given: " + args.get(1));
            }
        } else {
            log.info("Port not specified (2nd argument) using default: " + DEFAULT_PORT);
        }

        return port;
    }

    private List<File> checkFolder(List<String> list) {
        List<File> result = Lists.newArrayList();
        for (String string : list) {
            File file = new File(string);
            checkArgument(file.isDirectory(),"Given argument \"" + file + "\" is not a directory.");
            checkArgument(file.canRead(),"Can not read from given directory: " + file);
            result.add(file);
        }
        return result;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Folder {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Port {

    }


}
