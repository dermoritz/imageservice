package de.ml.endpoints;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mongodb.MongoDbEndpoint;

import com.mongodb.MongoClient;

public class PersistenceEndpointsProvider implements PersistenceEndpoints {

    private final CamelContext context;
    private MongoClient mongoClient;

    @Inject
    public PersistenceEndpointsProvider( CamelContext context ) {
        this.context = context;
    }



    @Override
    public Endpoint saveImageAccess() {
        return getMongoEndpoint();
    }

    private Endpoint getMongoEndpoint() {
        addMongoDbToRegistry();
        //MongoDbEndpoint mongoDbEndpoint = (MongoDbEndpoint) context.getComponent( "mongodb" ).createEndpoint( "mongodb:myDb" );
        MongoDbEndpoint mongoDbEndpoint = context.getEndpoint("mongodb:myDb", MongoDbEndpoint.class);
        //mongoDbEndpoint.setEndpointUriIfNotSpecified( "mongodb:myDb" );
        mongoDbEndpoint.setMongoConnection( mongoClient );
        mongoDbEndpoint.setDatabase( "imageservice" );
        mongoDbEndpoint.setCollection( "images" );
        mongoDbEndpoint.setOperation( "save" );
        //mongoDbEndpoint.setCamelContext( context );
        //context.addEndpoint( "mongodb:myDb", mongoDbEndpoint );


        return mongoDbEndpoint;
    }

    private void addMongoDbToRegistry() {
        mongoClient = new MongoClient();
        final CamelContext camelContext = context;
        final org.apache.camel.impl.SimpleRegistry registry = new org.apache.camel.impl.SimpleRegistry();
        final org.apache.camel.impl.CompositeRegistry compositeRegistry = new org.apache.camel.impl.CompositeRegistry();
        compositeRegistry.addRegistry(camelContext.getRegistry());
        compositeRegistry.addRegistry(registry);
        ((org.apache.camel.impl.DefaultCamelContext) camelContext).setRegistry(compositeRegistry);
        registry.put("myDb", mongoClient );

    }
}
