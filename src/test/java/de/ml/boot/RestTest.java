package de.ml.boot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class RestTest {

    private Future<?> task;

    @Before
    public void run() throws InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        task = exec.submit(()->org.jboss.weld.environment.se.StartMain.main(new String[] {"C:\\Users\\moritz\\Downloads\\1"}));
        //wait until camel started
        Thread.sleep(10000);
    }

    @Test
    public void testNext(){
        RestAssured.with()
            .authentication().basic("user1", "awdrg")
            .get("http://localhost/next").then().statusCode(200);
    }



}
