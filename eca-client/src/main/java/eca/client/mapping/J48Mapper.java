package eca.client.mapping;

import eca.client.dto.options.J48Options;
import eca.trees.J48;
import org.mapstruct.Mapper;

/**
 * Implements mapping J48 classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class J48Mapper extends AbstractClassifierMapper<J48, J48Options> {

    protected J48Mapper() {
        super(J48.class);
    }
}
