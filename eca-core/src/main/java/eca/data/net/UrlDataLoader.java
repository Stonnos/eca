/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.net;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.FileUtils;
import eca.data.file.XLSLoader;
import eca.data.file.resource.InputStreamResource;
import eca.util.Utils;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.JSONLoader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;


/**
 * Class for loading data from network using http and ftp protocols.
 *
 * @author Roman Batygin
 */
public class UrlDataLoader extends AbstractDataLoader<URL> {

    /**
     * Available files extensions
     */
    private static final String[] FILE_EXTENSIONS = DataFileExtension.getExtensions();

    /**
     * Available protocols
     **/
    private static final String[] PROTOCOLS = {"http", "ftp", "https", "ftps"};

    /**
     * Creates object with given <tt>URL</tt>
     *
     * @param url source url
     * @throws IllegalArgumentException if given url contains incorrect protocol or file extension
     */
    public UrlDataLoader(URL url) {
        this.setSource(url);
    }

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        URLConnection connection = getSource().openConnection();
        try (InputStream inputStream = connection.getInputStream()) {
            if (FileUtils.isXlsExtension(getSource().getFile())) {
                XLSLoader loader = new XLSLoader();
                loader.setResource(new InputStreamResource(inputStream));
                loader.setDateFormat(getDateFormat());
                data = loader.getDataSet();
            } else {
                AbstractFileLoader saver = createFileLoaderByUrl();
                saver.setSource(inputStream);
                data = saver.getDataSet();
            }
        }
        return data;
    }

    @Override
    protected void validateSource(URL url) {
        if (!Utils.contains(PROTOCOLS, url.getProtocol(), (x, y) -> x.equals(y))) {
            throw new IllegalArgumentException(String.format(UrlDataLoaderDictionary.BAD_PROTOCOL_ERROR_FORMAT,
                    Arrays.asList(PROTOCOLS)));
        }
        if (!Utils.contains(FILE_EXTENSIONS, url.getFile(), (x, y) -> x.endsWith(y))) {
            throw new IllegalArgumentException(String.format(UrlDataLoaderDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    Arrays.asList(FILE_EXTENSIONS)));
        }
    }


    private AbstractFileLoader createFileLoaderByUrl() {
        if (getSource().getFile().endsWith(DataFileExtension.CSV.getExtension())) {
            return new CSVLoader();
        } else if (getSource().getFile().endsWith(DataFileExtension.ARFF.getExtension())) {
            return new ArffLoader();
        } else if (getSource().getFile().endsWith(DataFileExtension.JSON.getExtension())) {
            return new JSONLoader();
        } else {
            throw new IllegalArgumentException(String.format("Unexpected file format: %s", getSource().getFile()));
        }
    }

}
