package eca.statistics.diagram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements table for selection recommended intervals number in frequency diagram.
 *
 * @author Roman Batygin
 */
public class FrequencyIntervalsTable {

    public static final int MIN_INTERVALS_NUM = 7;
    public static final int MAX_INTERVALS_NUM = 22;
    public static final int MIN_SAMPLE_SIZE = 40;
    public static final int MAX_SAMPLE_SIZE = 10000;

    private static List<Intervals> intervalsList;

    private static FrequencyIntervalsTable frequencyIntervalsTable;

    static {
        intervalsList = new ArrayList<>();
        intervalsList.add(new Intervals(new IntervalData(MIN_SAMPLE_SIZE, 100),
                new IntervalData(MIN_INTERVALS_NUM, 9)));
        intervalsList.add(new Intervals(new IntervalData(100, 500),
                new IntervalData(8, 12)));
        intervalsList.add(new Intervals(new IntervalData(500, 1000),
                new IntervalData(10, 16)));
        intervalsList.add(new Intervals(new IntervalData(1000, MAX_SAMPLE_SIZE),
                new IntervalData(12, MAX_INTERVALS_NUM)));
    }

    private FrequencyIntervalsTable() {
    }

    /**
     * Creates a singleton object.
     *
     * @return {@link FrequencyIntervalsTable} object
     */
    public static FrequencyIntervalsTable getFrequencyIntervalsTable() {
        if (frequencyIntervalsTable == null) {
            frequencyIntervalsTable = new FrequencyIntervalsTable();
        }
        return frequencyIntervalsTable;
    }

    /**
     * Returns the recommended intervals list.
     *
     * @return {@link List} of recommended intervals
     */
    public List<Intervals> getIntervals() {
        return intervalsList;
    }

    /**
     * Intervals model.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Intervals {

        private IntervalData sampleSizeInterval;

        private IntervalData intervalsNum;
    }

}
