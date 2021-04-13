/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.db;

import eca.data.db.model.DataBaseType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Datasource descriptor.
 *
 * @author Roman Batygin
 */
@Getter
public abstract class ConnectionDescriptor implements java.io.Serializable {

    private DataBaseType dataBaseType;
    @Setter
    private String host = StringUtils.EMPTY;
    @Setter
    private int port;
    @Setter
    private String dataBaseName = StringUtils.EMPTY;
    @Setter
    private String login = StringUtils.EMPTY;
    @Setter
    private String password = StringUtils.EMPTY;
    @Setter
    private String driver;

    protected ConnectionDescriptor(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    /**
     * Creates <code>ConnectionDescriptor</code> object with given options.
     *
     * @param host         datasource host
     * @param port         datasource port number
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    protected ConnectionDescriptor(DataBaseType dataBaseType,
                                   String host, int port, String dataBaseName,
                                   String login, String password) {
        this(dataBaseType);
        this.host = host;
        this.port = port;
        this.dataBaseName = dataBaseName;
        this.login = login;
        this.password = password;
    }

    /**
     * Returns url string of datasource.
     *
     * @return url string of datasource
     */
    public abstract String getUrl();

    /**
     * Returns connection protocol.
     *
     * @return connection protocol
     */
    public abstract String getProtocol();

}
