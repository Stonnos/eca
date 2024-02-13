package eca.data.file.resource;

import eca.data.file.FileDataDictionary;
import eca.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * Input stream resource wrapper.
 *
 * @author Roman Batygin
 */
public class UrlResource extends AbstractResource<URL> {

    /**
     * Available protocols
     **/
    private static final String[] PROTOCOLS = {"http", "ftp", "https", "ftps"};

    /**
     * Creates url resource object.
     *
     * @param resource - url resource
     */
    public UrlResource(URL resource) {
        super(resource);
        if (!Utils.contains(PROTOCOLS, resource.getProtocol(), String::equals)) {
            throw new IllegalArgumentException(String.format(FileDataDictionary.BAD_PROTOCOL_ERROR_FORMAT,
                    Arrays.asList(PROTOCOLS)));
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return getResource().openConnection().getInputStream();
    }

    @Override
    public String getFile() {
        return getResource().getFile();
    }

    @Override
    public String getExtension() {
        String extension = FilenameUtils.getExtension(getResource().getFile());
        int extEndIndex = StringUtils.indexOfAny(extension, "/[#?]/");
        if (extEndIndex > 0) {
            //end of extension may contains end-of-string or question-mark or hash-mark, remove them
            return extension.substring(0, extEndIndex);
        }
        return extension;
    }
}
