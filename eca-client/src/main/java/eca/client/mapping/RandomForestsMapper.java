package eca.client.mapping;

import eca.client.dto.options.RandomForestsOptions;
import eca.ensemble.forests.RandomForests;
import org.mapstruct.Mapper;

/**
 * Implements mapping Random Forests classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class RandomForestsMapper extends AbstractClassifierMapper<RandomForests, RandomForestsOptions> {

    protected RandomForestsMapper() {
        super(RandomForests.class);
    }
}
