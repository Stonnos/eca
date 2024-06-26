package eca.client.mapping;

import eca.client.dto.options.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Implements mapping heterogeneous classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class HeterogeneousClassifierMapper
        extends AbstractClassifierMapper<HeterogeneousClassifier, HeterogeneousClassifierOptions> {

    protected HeterogeneousClassifierMapper() {
        super(HeterogeneousClassifier.class);
    }

    @AfterMapping
    public void postMapping(HeterogeneousClassifier classifier, @MappingTarget HeterogeneousClassifierOptions options) {
        if (classifier instanceof ModifiedHeterogeneousClassifier) {
            options.setUseRandomSubspaces(true);
        }
    }
}
