package de.ml.boot;

import static de.ml.boot.Util.getFolderOfResourceFolder;

import org.junit.Test;

public class RunMain {

    @Test
    public void run() {
        org.jboss.weld.environment.se.StartMain.main(new String[] {getFolderOfResourceFolder("numbers")});
    }

    @Test
    public void runWithFolder() {
        org.jboss.weld.environment.se.StartMain.main(new String[] {"C:\\Users\\moritz\\Downloads\\1\\imageService\\examples1","8080"});
    }

}
