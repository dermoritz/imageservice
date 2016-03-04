package de.ml.boot;

import org.junit.Test;

import static de.ml.boot.Util.*;

public class runMain {

    @Test
    public void run() {
        org.jboss.weld.environment.se.StartMain.main(new String[] {getFolderOfResourceFolder("numbers")});
    }

}
