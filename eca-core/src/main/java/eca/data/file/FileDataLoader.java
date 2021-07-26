package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.arff.ArffFileLoader;
import eca.data.file.csv.CsvLoader;
import eca.data.file.json.JsonLoader;
import eca.data.file.resource.DataResource;
import eca.data.file.text.DATALoader;
import eca.data.file.text.DocxLoader;
import eca.data.file.xls.XLSLoader;
import eca.data.file.xml.XmlLoader;
import eca.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static eca.data.FileUtils.DOCX_EXTENSIONS;
import static eca.data.FileUtils.TXT_EXTENSIONS;
import static eca.data.FileUtils.XLS_EXTENSIONS;
import static eca.data.FileUtils.containsExtension;

/**
 * Class for loading input data from file.
 *
 * @author Roman Batygin
 */
@Slf4j
public class FileDataLoader extends AbstractDataLoader<DataResource> {

    private static final String[] FILE_EXTENSIONS = DataFileExtension.getExtensions();
    private static final String FILE_EXTENSION_FORMAT = ".%s";

    private static final List<LoaderConfig> LOADER_CONFIGS;

    static {
        LOADER_CONFIGS = newArrayList();
        LOADER_CONFIGS.add(
                new LoaderConfig(Collections.singleton(DataFileExtension.CSV.getExtendedExtension()), new CsvLoader()));
        LOADER_CONFIGS.add(new LoaderConfig(Collections.singleton(DataFileExtension.ARFF.getExtendedExtension()),
                new ArffFileLoader()));
        LOADER_CONFIGS.add(new LoaderConfig(TXT_EXTENSIONS, new DATALoader()));
        LOADER_CONFIGS.add(new LoaderConfig(XLS_EXTENSIONS, new XLSLoader()));
        LOADER_CONFIGS.add(new LoaderConfig(DOCX_EXTENSIONS, new DocxLoader()));
        LOADER_CONFIGS.add(
                new LoaderConfig(Collections.singleton(DataFileExtension.XML.getExtendedExtension()), new XmlLoader()));
        LOADER_CONFIGS.add(new LoaderConfig(Collections.singleton(DataFileExtension.JSON.getExtendedExtension()),
                new JsonLoader()));
    }

    /**
     * Data loader config.
     */
    @Data
    @AllArgsConstructor
    private static class LoaderConfig {
        Set<String> extensions;
        AbstractDataLoader<DataResource> dataLoader;
    }

    @Override
    public Instances loadInstances() throws Exception {
        log.info("Starting to load instances from [{}]", getSource().getFile());
        Instances data;
        LoaderConfig loaderConfig = LOADER_CONFIGS.stream()
                .filter(config -> containsExtension(getSource().getFile(), config.getExtensions()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't load data from file '%s'", getSource().getFile())));
        data = loadData(loaderConfig.getDataLoader());
        data.setClassIndex(data.numAttributes() - 1);
        log.info("Instances has been loaded from [{}]", getSource().getFile());
        return data;
    }

    @Override
    protected void validateSource(DataResource dataResource) {
        super.validateSource(dataResource);
        if (!Utils.contains(FILE_EXTENSIONS, dataResource.getFile(),
                (x, y) -> x.endsWith(String.format(FILE_EXTENSION_FORMAT, y)))) {
            throw new IllegalArgumentException(String.format(FileDataDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    Arrays.asList(FILE_EXTENSIONS)));
        }
    }

    private Instances loadData(AbstractDataLoader<DataResource> loader) throws Exception {
        loader.setSource(getSource());
        loader.setDateFormat(getDateFormat());
        return loader.loadInstances();
    }
}
