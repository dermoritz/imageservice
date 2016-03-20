package de.ml.endpoints;

import org.apache.camel.Endpoint;

/**
 * specifies all provided rest endpoints.
 * @author moritz
 *
 */
public interface RestEndpoints {

    Endpoint next();

    Endpoint nextAuto();

    Endpoint nextAutoTime();

    Endpoint update();

    Endpoint prev();

    Endpoint prevOffset();

    Endpoint info();

    Endpoint current();

    Endpoint filterName();

    Endpoint filterNameAuto();

    Endpoint filterNameInfo();

    Endpoint filterNameAutoTime();

    Endpoint filterNameSort();

    Endpoint filterNameAutoSort();

    Endpoint filterNameAutoTimeSort();

    Endpoint byIndex();
    
    Endpoint byIndexInfo();

    Endpoint byIndexFiltered();

    Endpoint maxIndex();

    Endpoint statisticAvgDistance();

    Endpoint statisticDistChart();
}