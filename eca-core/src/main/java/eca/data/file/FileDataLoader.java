package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.arff.ArffFileLoader;
import eca.data.file.csv.CsvLoader;
import eca.data.file.json.JsonLoader;
import eca.data.file.resource.DataResource;
import eca.data.file.resource.ZipEntryResource;
import eca.data.file.text.DATALoader;
import eca.data.file.xls.XLSLoader;
import eca.data.file.xml.XmlLoader;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import weka.core.Instances;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        if (getSource().getExtension().endsWith(DataFileExtension.ZIP.getExtension())) {
            @Cleanup InputStream inputStream = getSource().openInputStream();
            @Cleanup ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            Objects.requireNonNull(zipEntry, "Expected data file in zip archive");
            ZipEntryResource zipEntryResource = new ZipEntryResource(zipEntry, zipInputStream);
            LoaderConfig loaderConfig = getLoaderConfig(zipEntryResource);
            data = loadData(loaderConfig.getDataLoaderSupplier().get(), zipEntryResource);
        } else {
            LoaderConfig loaderConfig = getLoaderConfig(getSource());
            data = loadData(loaderConfig.getDataLoaderSupplier().get(), getSource());
        }
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

    private LoaderConfig getLoaderConfig(DataResource<?> dataResource) {
        return LOADER_CONFIGS.stream()
                .filter(config -> containsExtension(dataResource.getFile(), config.getExtensions()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't load data from file '%s'", dataResource.getFile())));
    }

    private Instances loadData(AbstractDataLoader<DataResource> loader, DataResource<?> dataResource)
            throws Exception {
        loader.setSource(dataResource);
        loader.setDateFormat(getDateFormat());
        return loader.loadInstances();
    }
}
