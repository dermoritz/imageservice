package de.ml.boot;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.camel.impl.SimpleRegistry;

public class RegistryProvider {
    @Singleton
    @Produces
    private SimpleRegistry getRegistry(){
        return new SimpleRegistry();
    }
}
