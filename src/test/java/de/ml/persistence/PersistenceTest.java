package de.ml.persistence;


import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.HashSet;

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

import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
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
import de.ml.persitence.Endpoints;
import de.ml.persitence.ImageDocument;
import de.ml.persitence.MongoAliveListener;

public class PersistenceTest extends CamelTestSupport {

    private static final String DIRECT_UPDATE = "direct:update";

    private static final String DIRECT_FIND_BY_ID = "direct:find_by_id";

    /**
     * If true the createImageDocument will use an embedded mongodb and will run independently.
     * Set to false to createImageDocument against a running mongo db
     */
    private static final Boolean useEmbedded = true;

    private static final String DIRECT_REMOVE = "direct:remove";

    @EndpointInject(uri = "mock:updateresult")
    protected MockEndpoint updateResultEndpoint;

    @EndpointInject(uri = "mock:findByIdResult")
    protected MockEndpoint findByIdResultEndpoint;

    @Produce(uri = DIRECT_UPDATE )
    protected ProducerTemplate updateTemplate;

    @Produce(uri = DIRECT_FIND_BY_ID)
    protected ProducerTemplate findByIdTemplate;

    @Produce(uri = DIRECT_REMOVE)
    protected ProducerTemplate removeTemplate;

    private static MongodExecutable mongodExecutable;

    private PersistenceEndpointsProvider provider;

    @EndpointInject(uri = "mock:remove")
    private MockEndpoint removedResult;

    @Test
    public void createImageDocument() throws InterruptedException, URISyntaxException {
        URL imageFileUrl = PersistenceTest.class.getResource( "/numbers/0.jpg" );
        Path path = Paths.get( imageFileUrl.toURI() );
        ImageDocument imageDocument = new ImageDocument( path );

        //persist an document
        persistAnDocument( imageDocument );

        //retrieve and check image document
        ImageDocument readImageDocument = getImageDocument( imageDocument );

        assertNotNull(readImageDocument.getLastUpdated());
        assertThat( readImageDocument.getLastUpdated(), lessThan( ZonedDateTime.now() ) );
        assertNull( readImageDocument.getLastFetched() );
        assertThat( readImageDocument.getCurrentPath(), is( path.toString() ) );

        //remove image
        removeImage( imageDocument );

    }


    @Test
    public void updateImageDocument() throws InterruptedException, URISyntaxException {
        URL imageFileUrl = PersistenceTest.class.getResource( "/numbers/0.jpg" );
        Path path = Paths.get( imageFileUrl.toURI() );
        ImageDocument imageDocument = new ImageDocument( path );
        //persist an document
        persistAnDocument( imageDocument );


        //update image document
        //expect 1 from persisting an image and the others for the updates
        updateResultEndpoint.setExpectedMessageCount(7);
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( Endpoints.BYINDEX )}, MongoDbConstants.UPSERT, false );
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( Endpoints.RANDOM )}, MongoDbConstants.UPSERT, false );
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( Endpoints.RANDOM )}, MongoDbConstants.UPSERT, false );
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.countAccess( Endpoints.BYFILTER )}, MongoDbConstants.UPSERT, false );
        HashSet<String> tags = Sets.newHashSet( "simp", "innie", "green" );
        HashSet<String> removedTags = Sets.newHashSet( "simp" );
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.addTags( tags )}, MongoDbConstants.UPSERT, false );
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.removeTags( removedTags )}, MongoDbConstants.UPSERT, false );
        updateResultEndpoint.assertIsSatisfied();

        ImageDocument readImageDocument = getImageDocument( imageDocument );
        assertThat( readImageDocument.getCountByEndpoint().get( Endpoints.BYINDEX ), is( 1 ) );
        assertThat( readImageDocument.getCountByEndpoint().get( Endpoints.RANDOM ), is( 2 ) );
        assertThat( readImageDocument.getCountByEndpoint().get( Endpoints.BYFILTER ), is( 1 ) );

        assertThat( readImageDocument.getTags(), is( Sets.difference( tags, removedTags ) ) );

        assertNotNull( readImageDocument.getLastFetched() );

        //remove image
        removeImage( imageDocument );

    }


    private void persistAnDocument( ImageDocument imageDocument ) throws InterruptedException {
        updateResultEndpoint.setExpectedMessageCount(1);
        updateTemplate.sendBodyAndHeader( new Object[]{imageDocument.get_id().asWrappedDBObject(), imageDocument.updateImage()}, MongoDbConstants.UPSERT, true );
        updateResultEndpoint.assertIsSatisfied();
        //check if persisted successfully
        UpdateResult updateResult = updateResultEndpoint.getExchanges().get( 0 ).getIn().getBody( UpdateResult.class );
        assertNotNull( updateResult );
    }

    private ImageDocument getImageDocument( ImageDocument imageDocument ) throws InterruptedException {
        findByIdResultEndpoint.setExpectedMessageCount( 1 );
        findByIdTemplate.sendBody( imageDocument.get_id().asDBObject() );
        findByIdResultEndpoint.assertIsSatisfied();
        //convert to ImageDocument
        BasicDBObject object = findByIdResultEndpoint.getExchanges().get( 0 ).getIn().getBody( BasicDBObject.class );
        assertNotNull(object);

        ImageDocument readImageDocument = provider.getMongoConverter().read( ImageDocument.class, object );
        assertNotNull(readImageDocument);
        return readImageDocument;
    }

    private void removeImage( ImageDocument imageDocument ) throws InterruptedException {
        removedResult.setExpectedMessageCount( 1 );
        removeTemplate.sendBody( imageDocument.get_id().asWrappedDBObject() );
        removedResult.assertIsSatisfied();
        Integer affectedCount = removedResult.getExchanges().get( 0 ).getIn().getHeader( MongoDbConstants.RECORDS_AFFECTED, Integer.class );
        assertThat( affectedCount, is( 1 ) );
    }

    @Override
    public boolean isUseRouteBuilder() {
        return true;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        provider = new PersistenceEndpointsProvider(context(), new MongoAliveListener(context()) );
        return new RouteBuilder() {
            @Override
            public void configure() {
                from( DIRECT_UPDATE )
                        .to( provider.updateImage()).to( updateResultEndpoint );
                from( DIRECT_FIND_BY_ID )
                        .to( provider.readById()).to( findByIdResultEndpoint );
                from( DIRECT_REMOVE)
                        .to( provider.removeById()).to( removedResult );

            }
        };
    }

    /**
     * Initializes embedded mongo db
     */
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
