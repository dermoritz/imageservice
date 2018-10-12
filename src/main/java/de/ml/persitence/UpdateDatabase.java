package de.ml.persitence;

import java.nio.file.Path;
import java.util.List;

public class UpdateDatabase implements Runnable {

    private final List<Path> files;

    public UpdateDatabase( List<Path> files) {
        this.files = files;
    }

    @Override
    public void run() {

    }
}
