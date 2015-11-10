package de.ml.boot;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.bindings.Parameters;
import org.slf4j.Logger;

@Singleton
public class ArgsConfiguration {

    private static final int DEFAULT_PORT = 80;
    private File folder;
    private List<String> args;
    @Inject
    private Logger log;

    @Inject
    private ArgsConfiguration(@Parameters List<String> args, Logger log) {
        this.args = args;
        checkArgument(args.size() > 0, "Missing argument to specify folder.");
        folder = checkFolder(args.get(0));

    }

    @Produces
    @Folder
    public File getFolder() {
        return folder;
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

    private File checkFolder(String string) {
        File file = new File(string);
        checkArgument(file.isDirectory(),"Given argument \"" + file + "\" is not a directory.");
        checkArgument(file.canRead(),"Can not read from given directory: " + file);
        return file;
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
