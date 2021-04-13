package eca.data.file;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.arff.ArffFileSaver;
import eca.data.file.csv.CsvSaver;
import eca.data.file.json.JsonSaver;
import eca.data.file.text.DATASaver;
import eca.data.file.text.DocxSaver;
import eca.data.file.xls.XLSSaver;
import eca.data.file.xml.XmlSaver;
import lombok.AllArgsConstructor;
import lombok.Data;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static eca.data.FileUtils.DOCX_EXTENSIONS;
import static eca.data.FileUtils.TXT_EXTENSIONS;
import static eca.data.FileUtils.XLS_EXTENSIONS;
import static eca.data.FileUtils.containsExtension;

/**
 * Class for saving {@link Instances} objects to file with extensions such as:
 * csv, arff, xls, xlsx, json, txt, data, xml, docx.
 *
 * @author Roman Batygin
 */
public class FileDataSaver {

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private static final List<SaverConfig> SAVE_CONFIGS;

    static {
        SAVE_CONFIGS = newArrayList();
        SAVE_CONFIGS.add(
                new SaverConfig(Collections.singleton(DataFileExtension.CSV.getExtendedExtension()), new CsvSaver()));
        SAVE_CONFIGS.add(
                new SaverConfig(Collections.singleton(DataFileExtension.ARFF.getExtendedExtension()),
                        new ArffFileSaver()));
        SAVE_CONFIGS.add(new SaverConfig(TXT_EXTENSIONS, new DATASaver()));
        SAVE_CONFIGS.add(new SaverConfig(XLS_EXTENSIONS, new XLSSaver()));
        SAVE_CONFIGS.add(new SaverConfig(DOCX_EXTENSIONS, new DocxSaver()));
        SAVE_CONFIGS.add(
                new SaverConfig(Collections.singleton(DataFileExtension.XML.getExtendedExtension()), new XmlSaver()));
        SAVE_CONFIGS.add(
                new SaverConfig(Collections.singleton(DataFileExtension.JSON.getExtendedExtension()), new JsonSaver()));
    }

    /**
     * Data saver config class.
     */
    @Data
    @AllArgsConstructor
    private static class SaverConfig {
        Set<String> extensions;
        AbstractDataSaver dataSaver;
    }

    /**
     * Returns date format.
     *
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Objects.requireNonNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Saves data to file.
     *
     * @param file - file object
     * @param data - instances object
     * @throws IOException in case of error
     */
    public void saveData(File file, Instances data) throws Exception {
        Objects.requireNonNull(file, "File is not specified!");
        Objects.requireNonNull(data, "Data is not specified!");
        SaverConfig saverConfig = SAVE_CONFIGS.stream()
                .filter(config -> containsExtension(file.getName(), config.getExtensions()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't save data %s to file '%s'", data.relationName(), file.getAbsoluteFile())));
        writeData(saverConfig.getDataSaver(), file, data);
    }

    private void writeData(AbstractDataSaver saver, File file, Instances data) throws Exception {
        saver.setFile(file);
        saver.setDateFormat(dateFormat);
        saver.write(data);
    }
}
