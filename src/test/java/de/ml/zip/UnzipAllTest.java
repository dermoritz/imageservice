package de.ml.zip;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by moritz on 27.11.2016.
 */
public class UnzipAllTest {

    @Test
    @Ignore
    public void unziptest() {
        UnzipAll to = new UnzipAll(Paths.get("C:\\Users\\moritz\\Downloads\\1\\unziptest"));
        to.run();
    }

}
