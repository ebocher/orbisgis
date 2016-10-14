package org.orbisgis.frameworkapi;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Core application parameters
 * @author Nicolas Fortin
 */
public interface CoreWorkspace extends Serializable {
    String PROP_APPLICATIONFOLDER = "applicationFolder";
    String PROP_WORKSPACEFOLDER = "workspaceFolder";
    String PROP_SOURCEFOLDER = "sourceFolder";
    String PROP_PLUGINFOLDER = "pluginFolder";
    String PROP_PLUGINCACHE = "pluginCache";
    String PROP_LOGFILE = "logFile";
    String VERSION_FILE = "org.orbisgis.version.txt";

    /**
     * Read the file located at {@link #getDataBaseUriFilePath()}
     * @return Content of the workspace database uri
     */
    String getJDBCConnectionReference();

    /**
     * Get the value of dataBaseUriFile
     *
     * @return the value of dataBaseUriFile or null if workspaceFolder is not defined
     */
    String getDataBaseUriFilePath();

    /**
     * @return The database user
     */
    String getDataBaseUser();

    /**
     * @return The database password
     */
    String getDataBasePassword();

    /**
     * @param user New user name for database connection
     */
    void setDataBaseUser(String user);

    /**
     * @param password New password for database connection
     */
    void setDataBasePassword(String password);

    /**
     *
     * @return True if the selected JDBC connection require a password. H2 doesn't require password by default.
     */
    boolean isRequirePassword();

    /**
     * @param requirePassword Is selected JDBC connection require a password
     */
    void setRequirePassword(boolean requirePassword);

    /**
     * Get the value of pluginCache
     *
     * @return the value of pluginCache
     */
    String getPluginCache();

    /**
     * Get the value of logFile
     *
     * @return the value of logFile
     */
    String getLogFile();

    /**
     * @return The full path of the log file
     */
    String getLogPath();

    /**
     * Read the workspace path list
     *
     * @return
     */
    List<File> readKnownWorkspacesPath();

    /**
     *
     * @return The default workspace folder or null if there is no default
     * workspace
     */
    File readDefaultWorkspacePath();

    /**
     * Get the value of applicationFolder
     *
     * @return the value of applicationFolder
     */
    String getApplicationFolder();

    /**
     * Get the value of tempFolder
     *
     * @return the value of tempFolder
     */
    String getTempFolder();

    /**
     * Get the value of pluginFolder
     *
     * @return the value of pluginFolder
     */
    String getPluginFolder();

    /**
     * Get the value of sourceFolder
     *
     * @return the value of sourceFolder
     */
    String getSourceFolder();

    /**
     * Get the value of workspaceFolder
     *
     * @return the value of workspaceFolder
     */
    String getWorkspaceFolder();

    /**
     * Add a property-change listener for all properties. The listener is
     * called for all properties.
     *
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener
     * instance
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a property-change listener for a specific property. The listener
     * is called only when there is a change to the specified property.
     *
     * @param prop The static property name PROP_..
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener
     * instance
     */
    void addPropertyChangeListener(String prop, PropertyChangeListener listener);

    /**
     * Remove the specified listener from the list
     *
     * @param listener The listener instance
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove the specified listener for a specified property from the list
     *
     * @param prop The static property name PROP_..
     * @param listener The listener instance
     */
    void removePropertyChangeListener(String prop, PropertyChangeListener listener);

    /**
     * @return Major application version
     */
    int getVersionMajor();

    /**
     * @return Minor application version
     */
    int getVersionMinor();

    /**
     * @return Revision version.
     */
    int getVersionRevision();

    /**
     * @return Version qualifier
     */
    String getVersionQualifier();
    
    /**
     * 
     * @return name of the database
     */
    String getDatabaseName();
    
    /**
     * Name of the database
     * @param databaseName 
     */
    void setDatabaseName(String databaseName);
    
}
