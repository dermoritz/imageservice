package de.ml.persistence;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.MongoClient;
import com.mongodb.client.result.UpdateResult;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import de.ml.endpoints.PersistenceEndpointsProvider;
import de.ml.persitence.ImageDocument;

public class PersistenceTest extends CamelTestSupport {

    public static final String DIRECT_UPDATE = "direct:update";

    public static final String DIRECT_FIND_BY_ID = "direct:find_by_id";

    private static final Boolean useEmbedded = true;

    @EndpointInject(uri = "mock:updateresult")
    protected MockEndpoint updateResultEndpoint;

    @EndpointInject(uri = "mock:findByIdResult")
    protected MockEndpoint findByIdResultEndpoint;

    @Produce(uri = DIRECT_UPDATE )
    protected ProducerTemplate template;

    @Produce(uri = DIRECT_FIND_BY_ID)
    protected ProducerTemplate findByIdTemplate;

    private static MongodExecutable mongodExecutable;
    private MongoClient mongoClient;
    private PersistenceEndpointsProvider provider;

    @Test
    public void test() throws InterruptedException, URISyntaxException {
        URL imageFileUrl = PersistenceTest.class.getResource( "/numbers/0.jpg" );
        Path path = Paths.get( imageFileUrl.toURI() );
        ImageDocument imageDocument = new ImageDocument( path );

        updateResultEndpoint.setExpectedMessageCount(1);
        template.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.updateImage()}, MongoDbConstants.UPSERT, true );
        updateResultEndpoint.assertIsSatisfied();
        UpdateResult updateResult = updateResultEndpoint.getExchanges().get( 0 ).getIn().getBody( UpdateResult.class );
        assertNotNull( updateResult );
        //        updateResultEndpoint.setExpectedMessageCount(4);
//        template.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( Endpoints.BYINDEX )}, MongoDbConstants.UPSERT, false );
//        template.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( Endpoints.RANDOM )}, MongoDbConstants.UPSERT, false );
//        template.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.addTags( Sets.newHashSet( "simp","innie" ) )}, MongoDbConstants.UPSERT, false );
//        updateResultEndpoint.assertIsSatisfied();
//        findByIdResultEndpoint.setExpectedMessageCount( 1 );
//        findByIdTemplate.sendBody( imageDocument.get_id().asDBObject() );
//        findByIdResultEndpoint.assertIsSatisfied();
//        BasicDBObject object = findByIdResultEndpoint.getExchanges().get( 0 ).getIn().getBody( BasicDBObject.class );
//        Assert.assertNotNull(object);
//        MongoConverter mongoConverter = provider.getMongoTemplate().getConverter();
//        GenericConversionService conversionService = (GenericConversionService) mongoConverter.getConversionService();
//        conversionService.addConverter( new ZonedDateTimeConverter()  );
//        ImageDocument read = mongoConverter.read( ImageDocument.class, object );
//        Assert.assertNotNull(read);
    }

    public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime>{

        @Override
        public ZonedDateTime convert( String source ) {
            return ZonedDateTime.parse( source, DateTimeFormatter.ISO_OFFSET_DATE_TIME );
        }
    }


    @Override
    public boolean isUseRouteBuilder() {
        return true;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        provider = new PersistenceEndpointsProvider(context());
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from( DIRECT_UPDATE )
                        .to( provider.updateImage()).to( updateResultEndpoint );
               // from( DIRECT_FIND_BY_ID )
                 //       .to( provider.readById()).to( findByIdResultEndpoint );

            }
        };
    }


    @BeforeClass
    public static void initDb() throws IOException {
        if (useEmbedded) {
            MongodStarter starter = MongodStarter.getDefaultInstance();
            String bindIp = "localhost";
            int port = 27017;
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                    .build();
            mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();
        }
    }

    @AfterClass
    public static void tearDownDb() {
        if (useEmbedded) {
            mongodExecutable.stop();
        }
    }

}
