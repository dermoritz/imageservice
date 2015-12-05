package de.ml.statistic;

import java.util.Map;

/**
 * Created by moritz on 05.12.2015.
 * Specifies statistics about indexes chosen.
 */
public interface Statistic {
    /**
     * updates statistic
     * @param index index to update statistic
     */
    void update(int index);

    /**
     * Resets statistic and sets max index.
     * @param count count of different indexes.
     */
    void setCount(int count);

    /**
     *
     * @return average distance between 2 indexes subsequently chosen.
     */
    float avgDistance();

    /**
     *
     * @return distribution of all indexes (index->count chosen)
     */
    Map<Integer,Integer> distribution();
}
