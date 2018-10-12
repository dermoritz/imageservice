package de.ml.persitence;

import java.nio.file.Path;
import java.util.List;

public interface PersistenceRepository {

    void updateAll( List<Path> files );
}
