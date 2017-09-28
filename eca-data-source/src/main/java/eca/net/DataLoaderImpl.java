/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.net;

import eca.converters.XLSLoader;
import eca.utils.Utils;
import org.springframework.util.Assert;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.function.BiPredicate;

import static eca.converters.DataFileExtension.*;


/**
 * Class for loading data from network using http and ftp protocols.
 *
 * @author Roman Batygin
 */
public class DataLoaderImpl implements DataLoader {

    /**
     * Available protocols
     **/
    private static final String[] PROTOCOLS = {"http", "ftp"};

    /**
     * Available files extensions
     **/
    private static final String[] FILE_EXTENSIONS = {XLS, XLSX, CSV, ARFF};

    /**
     * Source url
     **/
    private URL url;

    /**
     * Creates object with given <tt>URL</tt>
     *
     * @param url source url
     * @throws Exception if given url contains incorrect protocol or file extension
     */
    public DataLoaderImpl(URL url) throws Exception {
        this.setURL(url);
    }

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        URLConnection connection = url.openConnection();
        if (url.getFile().endsWith(FILE_EXTENSIONS[0]) || url.getFile().endsWith(FILE_EXTENSIONS[1])) {
            XLSLoader loader = new XLSLoader();
            loader.setInputStream(connection.getInputStream());
            data = loader.getDataSet();
        } else {
            AbstractFileLoader saver = url.getFile().endsWith(FILE_EXTENSIONS[2])
                    ? new CSVLoader() : new ArffLoader();
            saver.setSource(connection.getInputStream());
            data = saver.getDataSet();
        }
        return data;
    }

    /**
     * Sets source url.
     *
     * @param url source url
     * @throws Exception if given url contains incorrect protocol or file extension
     */
    public final void setURL(URL url) throws Exception {
        Assert.notNull(url, "URL is not specified!");
        if (!Utils.contains(PROTOCOLS, url.getProtocol(), (x, y) -> x.equals(y))) {
            throw new Exception(String.format(DataLoaderDictionary.BAD_PROTOCOL_ERROR_FORMAT,
                    Arrays.asList(PROTOCOLS)));
        }
        if (!Utils.contains(FILE_EXTENSIONS, url.getFile(), (x, y) -> x.endsWith(y))) {
            throw new Exception(String.format(DataLoaderDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    Arrays.asList(FILE_EXTENSIONS)));
        }
        this.url = url;
    }

}
