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

    private File folder;

    @Inject
    private ArgsConfiguration(@Parameters List<String> args, Logger log) {
        checkArgument(args.size() > 0);
        folder = checkFolder(args.get(0));

    }

    @Produces
    @Folder
    public File getFolder() {
        return folder;
    }


    private File checkFolder(String string) {
        File file = new File(string);
        checkArgument(file.isDirectory());
        checkArgument(file.canRead());
        return file;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Folder {

    }


}
