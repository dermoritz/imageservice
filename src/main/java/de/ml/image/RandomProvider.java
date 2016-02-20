package de.ml.image;

import javax.enterprise.inject.Produces;
import java.util.Random;

/**
 * Created by moritz on 20.02.2016.
 */
public class RandomProvider {

    @Produces
    private Random getRandom(){
        return new Random();
    }

}
