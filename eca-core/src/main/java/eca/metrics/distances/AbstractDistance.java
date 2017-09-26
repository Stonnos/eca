package eca.metrics.distances;

/**
 * Distance function abstract class.
 * @author Roman Batygin
 */

public abstract class AbstractDistance implements Distance {

    private DistanceType distanceType;

    protected AbstractDistance(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    @Override
    public DistanceType getDistanceType() {
        return distanceType;
    }
}
