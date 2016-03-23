package de.ml.boot;

import org.junit.Test;

import static de.ml.boot.Util.*;

public class RunMain {

    @Test
    public void run() {
        org.jboss.weld.environment.se.StartMain.main(new String[] {getFolderOfResourceFolder("numbers")});
    }

    @Test
    public void runWithFolder() {
        org.jboss.weld.environment.se.StartMain.main(new String[] {"C:\\Users\\moritz\\Downloads\\1"});
    }

}
