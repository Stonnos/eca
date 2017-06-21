/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.net;

import weka.core.Instances;
import java.net.URLConnection;
import java.util.function.BiPredicate;
import java.net.URL;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffLoader;
import eca.core.converters.XLSLoader;
/**
 *
 * @author Roman93
 */
public class DataLoaderImpl implements DataLoader {

    private static final String[] PROTOCOLS = {"http", "ftp"};

    private static final String[] FILE_EXTENSIONS = {".xls", ".xlsx", ".csv", ".arff"};

    private URL url;
    
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
        }
        else {
            AbstractFileLoader saver = url.getFile().endsWith(FILE_EXTENSIONS[2])
                    ? new CSVLoader() : new ArffLoader();
            saver.setSource(connection.getInputStream());
            data = saver.getDataSet();
        }
        return data;
    }

    public final void setURL(URL url) throws Exception {
        if (url == null) {
            throw new IllegalArgumentException();
        }
        if (!contains(PROTOCOLS, url.getProtocol(), (x, y) -> x.equals(y))) {
            throw new Exception("Протокол соединения должен быть http или ftp!");
        }
        if (!contains(FILE_EXTENSIONS, url.getFile(), (x, y) -> x.endsWith(y))) {
            throw new Exception("Допускаются только файлы форматов: xls, xlsx, csv, arff!");
        }
        this.url = url;
    }

    private boolean contains(String[] list, String val, BiPredicate<String, String> predicate) {
        for (int i = 0; i < list.length; i++) {
            if (predicate.test(val, list[i])) {
                return true;
            }
        }
        return false;
    }

}
