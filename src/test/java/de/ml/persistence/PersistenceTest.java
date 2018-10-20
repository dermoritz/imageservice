package de.ml.persistence;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import de.ml.persitence.ImageDocument;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.*;

import java.io.IOException;
import java.net.UnknownHostException;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class PersistenceTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = DIRECT_IN)
    protected ProducerTemplate template;

    private ObjectMapper om = new ObjectMapper();
    private static MongodExecutable mongodExecutable;

    @Test
    public void test() throws InterruptedException {
        ImageDocument random = random(ImageDocument.class);
        resultEndpoint.setExpectedMessageCount(1);
        template.sendBody(random);
        resultEndpoint.assertIsSatisfied();
    }


    @Override
    public boolean isUseRouteBuilder() {
        return true;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(DIRECT_IN).process(exchange -> {
                    exchange.getIn().setBody(om.writeValueAsString(exchange.getIn().getBody()));
                }).to(getMongoEndpoint()).to(resultEndpoint);
            }
        };
    }


    private Endpoint getMongoEndpoint() {
        addMongoDbToRegistry();
        MongoDbEndpoint mongoDbEndpoint = context.getEndpoint("mongodb:myDb?database=imageservice&collection=images&operation=save", MongoDbEndpoint.class);

        return mongoDbEndpoint;
    }

    private void addMongoDbToRegistry() {
        MongoClient mongoClient = new MongoClient();
        final CamelContext camelContext = context();
        final org.apache.camel.impl.SimpleRegistry registry = new org.apache.camel.impl.SimpleRegistry();
        final org.apache.camel.impl.CompositeRegistry compositeRegistry = new org.apache.camel.impl.CompositeRegistry();
        compositeRegistry.addRegistry(camelContext.getRegistry());
        compositeRegistry.addRegistry(registry);
        ((org.apache.camel.impl.DefaultCamelContext) camelContext).setRegistry(compositeRegistry);
        registry.put("myDb", mongoClient);

    }

    @BeforeClass
    public static void initDb() throws IOException {
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

    @AfterClass
    public static void tearDownDb() {
        mongodExecutable.stop();
    }

}
