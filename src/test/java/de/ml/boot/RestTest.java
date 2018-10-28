package de.ml.boot;

import static de.ml.boot.Util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class RestTest {

    public static final String PORT = "8183";
    private Future<?> task;

    @Before
    public void run() throws InterruptedException {
        String resourcefolder = getFolderOfResourceFolder( "" );
        ExecutorService exec = Executors.newSingleThreadExecutor();
        task = exec.submit( ( ) -> org.jboss.weld.environment.se.StartMain.main( new String[] { resourcefolder, PORT } ) );
        // wait until camel started
        Thread.sleep( 10000 );
    }

    @Test
    public void testNext() {
        RestAssured.with()
            .get( "http://127.0.0.1:" + PORT + "/next" ).then().statusCode( 200 );
    }

}
