package de.ml.endpoints;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mongodb.MongoDbEndpoint;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

public class PersistenceEndpointsProvider implements PersistenceEndpoints {

    public static final String COLLECTION = "images";
    public static final String DATABASE = "imageservice";
    private final CamelContext context;
    private MongoClient mongoClient;
    private MongoTemplate mongoTemplate;

    @Inject
    public PersistenceEndpointsProvider( CamelContext context ) {
        this.context = context;
        addMongoDbToRegistry();
    }



    @Override
    public Endpoint updateImage() {
        return getMongoUpdateEndpoint();
    }

    @Override
    public Endpoint readById() {
        MongoDbEndpoint mongoDbEndpoint = context.getEndpoint("mongodb:myDb", MongoDbEndpoint.class);
        mongoDbEndpoint.setMongoConnection( mongoClient );
        mongoDbEndpoint.setDatabase( DATABASE );
        mongoDbEndpoint.setCollection( COLLECTION );
        mongoDbEndpoint.setOperation( "findById" );
        return mongoDbEndpoint;
    }

    private Endpoint getMongoUpdateEndpoint() {
        MongoDbEndpoint mongoDbEndpoint = context.getEndpoint("mongodb:myDb", MongoDbEndpoint.class);
        mongoDbEndpoint.setMongoConnection( mongoClient );
        mongoDbEndpoint.setDatabase( DATABASE );
        mongoDbEndpoint.setCollection( COLLECTION );
        mongoDbEndpoint.setOperation( "update" );
        return mongoDbEndpoint;
    }

    private void addMongoDbToRegistry() {
        mongoClient = new MongoClient();
        mongoTemplate = new MongoTemplate( mongoClient, DATABASE );
        final CamelContext camelContext = context;
        final SimpleRegistry registry = new SimpleRegistry();
        final CompositeRegistry compositeRegistry = new CompositeRegistry();
        compositeRegistry.addRegistry(camelContext.getRegistry());
        compositeRegistry.addRegistry(registry);
        ((DefaultCamelContext) camelContext).setRegistry(compositeRegistry);
        registry.put("myDb", mongoClient );
    }

    public MongoTemplate getMongoTemplate(){
        return mongoTemplate;
    }

}
