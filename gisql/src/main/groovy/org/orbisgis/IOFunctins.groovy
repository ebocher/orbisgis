package org.orbisgis

import groovy.sql.Sql
import org.h2gis.functions.io.utility.IOMethods

import java.sql.SQLException
import java.util.logging.Logger

class IOFunctins {

    static final Logger LOG = Logger.getLogger(IOFunctins.class.getName())

    private static final IOMethods iOMethods = new IOMethods();

/**
 * Export the given table from the given datastore into a file with the encoding.
 *
 * @param sql DataStore containing the table.
 * @param tableName Name of the table to save.
 * @param filePath Path of the destination file.
 * @param encoding Encoding of the file. Set to null by default.
 * @param deleteFile True to delete the file if exists. Set to true by default.
 * @throws java.sql.SQLException Exception throw on database error.  */
    static String[] export(Sql sql, String tableName, String filePath,
                           String encoding, boolean deleteFile)
            throws SQLException {
            return iOMethods.exportToFile(BaseUtils.getSQLConnection(sql), tableName, filePath, encoding, deleteFile)

    }

/**
 * Export the given table from the given datastore into a file with the encoding.
 *
 * @param sql DataStore containing the table.
 * @param tableName Name of the table to save.
 * @param filePath Path of the destination file.
 * @param encoding Encoding of the file. Set to null by default.
 * @param deleteFile True to delete the file if exists. Set to true by default.
 * @throws java.sql.SQLException Exception throw on database error.
 */
    static String[] export(Sql sql, String tableName, String filePath,
                           boolean deleteFile = false)
            throws SQLException {
       return iOMethods.exportToFile(BaseUtils.getSQLConnection(sql), tableName, filePath, null, deleteFile)
    }


/**
 * Import a file to a database
 *
 * @param sql DataStore containing the table.
 * @param filePath Path of the destination file.
 * @param tableName Name of the table to save.
 * @param encoding Encoding of the file. Set to null by default.
 * @param deleteFile True to delete the file if exists. Set to true by default.
 * @throws java.sql.SQLException Exception throw on database error.
 */
    static String[] "import"(Sql sql, String filePath, String tableName,
                             String encoding = null, boolean deleteFile = true)
            throws SQLException {
            return iOMethods.importFile(BaseUtils.getSQLConnection(sql), filePath, tableName, encoding, deleteFile)
    }

/**
 * Import a file to a database
 *
 * @param sql DataStore containing the table.
 * @param filePath Path of the destination file.
 * @param tableName Name of the table to save.
 * @param encoding Encoding of the file. Set to null by default.
 * @param deleteFile True to delete the file if exists. Set to true by default.
 * @throws java.sql.SQLException Exception throw on database error.
 */
    static String[] "import"(Sql sql, String filePath, String tableName,
                             boolean deleteFile)
            throws SQLException {
            return iOMethods.importFile(BaseUtils.getSQLConnection(sql), filePath, tableName, null, deleteFile)
    }


/**
 * Link a table from another database to an H2GIS database.
 *
 * @param sql The DataStore that will received the table.
 * @param params External database connection parameters to set up a link to the DataStore.
 * @param sourceTable The name of the table in the external database.
 * @param targetTable The name of the table in the DataStore.
 * @param delete True to delete the table if exists. Default to true.
 * @throws java.sql.SQLException Exception throw on database error.
 */
    static String linkedTable(Sql sql, Map<String, String> params, String sourceTable,
                              String targetTable, boolean delete = false) throws SQLException {
        return IOMethods.linkedTable(BaseUtils.getSQLConnection(sql), params, sourceTable, targetTable, delete)
    }

/**
 * Create a dynamic link from a file to a H2GIS database.
 *
 * @param sql connection to the database.
 * @param path The path of the file.
 * @param table The name of the table created to store the file.
 * @param delete True to delete the table if exists. Default to true.
 * @throws java.sql.SQLException Exception throw on database error.
 */
    static String linkedFile(Sql sql, String path, String table, boolean delete = false) throws SQLException {
        return IOMethods.linkedFile(BaseUtils.getSQLConnection(sql), path, table, delete)
    }

/**
 * Method to export a table into another database.
 *
 * @param sqlSource Source connection.
 * @param sourceTable The name of the table to export or a select query.
 * @param sqlTarget Target connection.
 * @param targetTable Target table name.
 * @param mode -1 delete the target table if exists and create a new table, 0 create a new table,
 *                        1 update the target table if exists.
 * @param batchSize Batch size value before sending the data.
 *
 * @throws java.sql.SQLException Exception throw on database error.
 */
    static String exportToDataBase(Sql sqlSource, String sourceTable,
                                   Sql sqlTarget, String targetTable,
                                   int mode = 0, int batchSize = 100) throws SQLException {
        return IOMethods.exportToDataBase(BaseUtils.getSQLConnection(sqlSource), sourceTable,
                BaseUtils.getSQLConnection(sqlTarget), targetTable,
                mode, batchSize)
    }
}
