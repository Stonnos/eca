package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.arff.ArffFileLoader;
import eca.data.file.csv.CsvLoader;
import eca.data.file.json.JsonLoader;
import eca.data.file.resource.DataResource;
import eca.data.file.text.DATALoader;
import eca.data.file.xls.XLSLoader;
import eca.data.file.xml.XmlLoader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import weka.core.Instances;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static eca.data.FileUtils.ALL_EXTENSIONS;
import static eca.data.FileUtils.TXT_EXTENSIONS;
import static eca.data.FileUtils.XLS_EXTENSIONS;
import static eca.data.FileUtils.containsExtension;
import static eca.data.FileUtils.isValidTrainDataFile;

/**
 * Class for loading input data from file.
 *
 * @author Roman Batygin
 */
@Slf4j
public class FileDataLoader extends AbstractDataLoader<DataResource> {

    private static final List<LoaderConfig> LOADER_CONFIGS;

    static {
        LOADER_CONFIGS = newArrayList();
        LOADER_CONFIGS.add(
                new LoaderConfig(Collections.singleton(DataFileExtension.CSV.getExtendedExtension()), CsvLoader::new));
        LOADER_CONFIGS.add(new LoaderConfig(Collections.singleton(DataFileExtension.ARFF.getExtendedExtension()),
                ArffFileLoader::new));
        LOADER_CONFIGS.add(new LoaderConfig(TXT_EXTENSIONS, DATALoader::new));
        LOADER_CONFIGS.add(new LoaderConfig(XLS_EXTENSIONS, XLSLoader::new));
        LOADER_CONFIGS.add(
                new LoaderConfig(Collections.singleton(DataFileExtension.XML.getExtendedExtension()), XmlLoader::new));
        LOADER_CONFIGS.add(new LoaderConfig(Collections.singleton(DataFileExtension.JSON.getExtendedExtension()),
                JsonLoader::new));
    }

    /**
     * Data loader config.
     */
    @Data
    @AllArgsConstructor
    private static class LoaderConfig {
        Set<String> extensions;
        Supplier<AbstractDataLoader<DataResource>> dataLoaderSupplier;
    }

    @Override
    public Instances loadInstances() throws Exception {
        log.info("Starting to load instances from [{}]", getSource().getFile());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Instances data;
        LoaderConfig loaderConfig = LOADER_CONFIGS.stream()
                .filter(config -> containsExtension(getSource().getFile(), config.getExtensions()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't load data from file '%s'", getSource().getFile())));
        data = loadData(loaderConfig.getDataLoaderSupplier().get());
        stopWatch.stop();
        log.info("Instances has been loaded from [{}] in {} ms.", getSource().getFile(),
                stopWatch.getTime(TimeUnit.MILLISECONDS));
        return data;
    }

    @Override
    protected void validateSource(DataResource dataResource) {
        super.validateSource(dataResource);
        if (!isValidTrainDataFile(dataResource.getFile())) {
            throw new IllegalArgumentException(String.format(FileDataDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    ALL_EXTENSIONS));
        }
    }

    private Instances loadData(AbstractDataLoader<DataResource> loader) throws Exception {
        loader.setSource(getSource());
        loader.setDateFormat(getDateFormat());
        return loader.loadInstances();
    }
}
