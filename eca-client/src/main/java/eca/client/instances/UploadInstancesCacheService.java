package eca.client.instances;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.InstancesDataModel;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Upload instances cache service.
 *
 * @author Roman Batygin
 */
@Slf4j
public class UploadInstancesCacheService {

    @Getter
    private final UploadInstancesClient uploadInstancesClient = new UploadInstancesClient();
    private final InstancesConverter instancesConverter = new InstancesConverter();
    private final Map<String, InstancesCacheValue> uploadedDataMap = newHashMap();

    /**
     * Instances cache value model.
     */
    @Data
    @RequiredArgsConstructor
    private static class InstancesCacheValue {

        /**
         * Last modification count
         */
        private final int lastModificationCount;

        /**
         * External data uuid in central data storage
         */
        private final String externalDataUuid;
    }

    /**
     * Uploads instances model to central data storage.
     *
     * @param instancesDataModel - instances data model
     * @return data uuid
     */
    public String uploadInstances(InstancesDataModel instancesDataModel) {
        log.debug("Starting to upload instances [{}] to central data storage",
                instancesDataModel.getData().relationName());
        InstancesCacheValue instancesCacheValue = uploadedDataMap.get(instancesDataModel.getUuid());
        if (instancesCacheValue == null ||
                instancesCacheValue.getLastModificationCount() != instancesDataModel.getLastModificationCount()) {
            InstancesModel instancesModel = instancesConverter.convert(instancesDataModel.getData());
            String dataUuid = uploadInstancesClient.uploadInstances(instancesModel);
            instancesCacheValue = new InstancesCacheValue(instancesDataModel.getLastModificationCount(), dataUuid);
            uploadedDataMap.put(instancesDataModel.getUuid(), instancesCacheValue);
        }
        return instancesCacheValue.getExternalDataUuid();
    }
}
