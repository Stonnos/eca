package eca.ensemble;

/**
 * Interface for classifier that can be built in multi threads mode.
 *
 * @author Roman Batygin
 */
public interface ConcurrentClassifier {

    /**
     * Sets the number of threads which used for building classifier model.
     *
     * @param numThreads the number of threads
     */
    void setNumThreads(Integer numThreads);

    /**
     * Returns the number of threads which used for building classifier model.
     * The null value means that classifier will be built in single thread.
     *
     * @return the number of threads which used for building classifier model.
     */
    Integer getNumThreads();
}
