package de.ml.boot;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.main.Main;

public class CamelMain extends Main {

    private CamelContext context;

    @Inject
    private CamelMain(CamelContext context){
        this.context = context;

    }

    @Override
    protected CamelContext createContext() {
        return context;
    }

}
