package de.ml.persitence.mongo;

import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.mongodb.MongoClient;

import de.ml.persitence.PersistenceRepository;

public class MongoPersistence implements PersistenceRepository {

    @Inject
    private Logger log;

    private MongoClient mongoClient = null;

    public MongoPersistence() {
            mongoClient = new MongoClient();
    }

    @Override
    public void updateAll( List<Path> files ) {

    }
}
