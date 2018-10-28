package de.ml.persistence;


import static io.github.benas.randombeans.api.EnhancedRandom.*;

import java.io.IOException;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;

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

    public static final String DIRECT_IN = "direct:in";

    private static final Boolean useEmbedded = false;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = DIRECT_IN)
    protected ProducerTemplate template;

    private ObjectMapper om = new ObjectMapper();
    private static MongodExecutable mongodExecutable;
    private MongoClient mongoClient;

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
        PersistenceEndpointsProvider provider = new PersistenceEndpointsProvider(context());
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(DIRECT_IN)
                        .marshal().json(JsonLibrary.Jackson).convertBodyTo(String.class)
                        .to(provider.saveImageAccess()).to(resultEndpoint);
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
