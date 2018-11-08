package de.ml.endpoints;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mongodb.MongoDbEndpoint;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.mongodb.MongoClient;

import de.ml.persitence.ZonedDateTimeConverter;

public class PersistenceEndpointsProvider implements PersistenceEndpoints {

    public static final String COLLECTION = "images";
    public static final String DATABASE = "imageservice";
    private final CamelContext context;
    private MongoClient mongoClient;
    private MongoConverter mongoConverter;

    @Inject
    public PersistenceEndpointsProvider( CamelContext context ) {
        this.context = context;
        addMongoDbToRegistry();
        addConverters();
    }

    @Override
    public Endpoint updateImage() {
        return getEndpoint( "update" );
    }

    @Override
    public Endpoint readById() {
        return getEndpoint( "findById" );
    }

    @Override
    public Endpoint removeById() {
        return getEndpoint( "remove" );
    }

    private Endpoint getEndpoint( String operation ) {
        //since MongoDbEndpoint are singleton it is important to have different url per endpoint to get different instances
        MongoDbEndpoint mongoDbEndpoint = context.getEndpoint("mongodb:myDb?operation="+ operation, MongoDbEndpoint.class );
        mongoDbEndpoint.setMongoConnection( mongoClient );
        mongoDbEndpoint.setDatabase( DATABASE );
        mongoDbEndpoint.setCollection( COLLECTION );
        return mongoDbEndpoint;
    }

    private void addMongoDbToRegistry() {
        mongoClient = new MongoClient();

        final CamelContext camelContext = context;
        final SimpleRegistry registry = new SimpleRegistry();
        final CompositeRegistry compositeRegistry = new CompositeRegistry();
        compositeRegistry.addRegistry(camelContext.getRegistry());
        compositeRegistry.addRegistry(registry);
        ((DefaultCamelContext) camelContext).setRegistry(compositeRegistry);
        registry.put("myDb", mongoClient );
    }

    private void addConverters() {
        mongoConverter = new MongoTemplate( mongoClient, DATABASE ).getConverter();
        ((GenericConversionService) mongoConverter.getConversionService()).addConverter( new ZonedDateTimeConverter()  );
    }

    @Produces
    public MongoConverter getMongoConverter(){
        return mongoConverter;
    }

}
