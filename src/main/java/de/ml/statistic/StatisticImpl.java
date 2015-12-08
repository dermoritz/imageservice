package de.ml.statistic;

import com.google.common.util.concurrent.AtomicLongMap;
import de.ml.routes.RestRoute;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.inject.Singleton;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by moritz on 05.12.2015.
 */
@Singleton
public class StatisticImpl implements Statistic, Serializable, Processor {

    /**
     * Stores current distribution.
     */
    private AtomicLongMap<Integer> distribution = AtomicLongMap.create();

    public static final String AVG_DISTANCE_ENDPOINT = "avgDistance";

    private Float avgDistance;

    private Integer lastIndex;

    private Integer count;

    private long callCount = 0;

    @Override
    public void update( int index ) {
        distribution.incrementAndGet( index );
        updateAvgDistance( index );
    }

    @Override
    public void setCount( int count ) {
        lastIndex = null;
        avgDistance = null;
        distribution = AtomicLongMap.create();
        this.count = count;
        callCount = 0;
    }

    private void updateAvgDistance( int index ) {
        // 2 phases, 1st call set index, 2nd call get first distance
        if( lastIndex != null ) {
            int distance = Math.abs( lastIndex - index );
            if( avgDistance != null ) {
                avgDistance = avgDistance + ( ( distance - avgDistance ) / ++callCount );
            } else {
                avgDistance = (float) distance;
            }
        }
        lastIndex = index;
    }

    @Override
    public float avgDistance() {
        return 0;
    }

    @Override
    public Map<Integer, Integer> distribution() {
        return null;
    }

    @Override
    public void process( Exchange exchange ) throws Exception {
        String url = exchange.getIn().getHeader( RestRoute.HTTP_URI_HEADER, String.class );
        if( url != null && url.toLowerCase().endsWith( AVG_DISTANCE_ENDPOINT.toLowerCase() ) ) {
            exchange.getIn().setBody( "average distance: " + avgDistance + " expected Distance: " + count / 3f );
        }

    }

}
