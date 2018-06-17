package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.client.json.InstancesSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

/**
 * Instances request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancesRequest {

    /**
     * Training data
     */
    @JsonSerialize(using = InstancesSerializer.class)
    private Instances data;
}
